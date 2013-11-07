/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// View.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 9:45:39 AM
////////

package net.kerious.engine.view;

import net.kerious.engine.drawable.Drawable;
import net.kerious.engine.renderer.DrawingContext;
import net.kerious.engine.renderer.Projection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.SnapshotArray;

public class View {
	
	////////////////////////
	// VARIABLES
	////////////////
	
	final public static int ORIGIN_BOTTOM_LEFT = 0;
	final public static int ORIGIN_BOTTOM_RIGHT = 1;
	final public static int ORIGIN_TOP_LEFT = 2;
	final public static int ORIGIN_TOP_RIGHT = 3;
	final public static int ORIGIN_CENTER = 4;
	final public static int ORIGIN_CUSTOM = 5;
	
	final private static Color tmpColor = new Color();
	
	final private SnapshotArray<View> views;
	final private Rectangle frame;
	final private Color tint;
	final private Rectangle renderingBounds;
	
	final private Matrix3 localTransformMatrix;
	final private Matrix3 drawingTransformMatrix;
	final private Matrix4 contextTransformMatrix;
	final private Matrix4 savedTransformMatrix;
	
	private Drawable background;
	private View parentView;
	private Projection projection;
	private float rotation;
	private float scaleX;
	private float scaleY;
	private float originX;
	private float originY;
	private boolean hidden;
	private boolean clipViews;
	private boolean shouldBeDrawn;
	private boolean transformMatrixDirty;
	private int originType;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public View() {
		this.views = new SnapshotArray<View>(true, 4, View.class);
		this.renderingBounds = new Rectangle();
		this.frame = new Rectangle();
		this.tint = new Color(Color.WHITE);
		this.setTint(this.tint);
		this.localTransformMatrix = new Matrix3();
		this.drawingTransformMatrix = new Matrix3();
		this.contextTransformMatrix = new Matrix4();
		this.savedTransformMatrix = new Matrix4();
		this.scaleX = 1;
		this.scaleY = 1;
		this.originType = ORIGIN_BOTTOM_LEFT;
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Inherit class should override this to implement the drawing
	 * @param batch
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawInRect(DrawingContext context, float x, float y, float width, float height, float alpha) {
		
	}
	
	private Matrix3 computeTransformMatrix() {
		if (this.transformMatrixDirty) {
			boolean originChanged = this.originType != ORIGIN_BOTTOM_LEFT;
			float transformOriginX = 0;
			float transformOriginY = 0;
			
			if (originChanged) {
				switch (this.originType) {
				case ORIGIN_BOTTOM_RIGHT:
					transformOriginX = this.frame.width;
					break;
				case ORIGIN_TOP_LEFT:
					transformOriginY = this.frame.height;
					break;
				case ORIGIN_TOP_RIGHT:
					transformOriginX = this.frame.width;
					transformOriginY = this.frame.height;
					break;
				case ORIGIN_CENTER:
					transformOriginX = this.frame.width / 2;
					transformOriginY = this.frame.height / 2;
					break;
				case ORIGIN_CUSTOM:
					transformOriginX = this.originX;
					transformOriginY = this.originY;
					break;
				}
				this.localTransformMatrix.setToTranslation(transformOriginX, transformOriginY);
			} else {
				this.localTransformMatrix.idt();
			}

			if (this.rotation != 0) {
				this.localTransformMatrix.rotate(this.rotation);
			}
			
			if (this.scaleX != 1 || this.scaleY != 1) {
				this.localTransformMatrix.scale(this.scaleX, this.scaleY);
			}
			
			if (originChanged) {
				this.localTransformMatrix.translate(-transformOriginX, -transformOriginY);
			}
			
			this.localTransformMatrix.trn(this.frame.x, this.frame.y);
			this.transformMatrixDirty = false;
		}
		
		if (this.parentView == null) {
			this.drawingTransformMatrix.set(this.localTransformMatrix);
		} else {
			this.drawingTransformMatrix.set(this.parentView.drawingTransformMatrix);
			this.drawingTransformMatrix.mul(this.localTransformMatrix);
		}
		
		return this.drawingTransformMatrix;
	}
	
	private void applyTransformToContext(DrawingContext context) {
		Matrix3 transformMatrix = this.computeTransformMatrix();
		
		this.savedTransformMatrix.set(context.getTransformMatrix());
		
		this.contextTransformMatrix.set(transformMatrix);
		context.setTransformMatrix(this.contextTransformMatrix);
	}
	
	private void restoreTransformToContext(DrawingContext context) {
		context.setTransformMatrix(this.savedTransformMatrix);
	}
	
	final public boolean draw(DrawingContext context, float parentAlpha) {
		if (!this.shouldBeDrawn) {
			return false;
		}
		
		this.renderingBounds.set(this.frame.x, this.frame.height, this.frame.width, this.frame.height);
		
		if (!context.isVisibleInContext(this.renderingBounds)) {
			return false;
		}
		
		final boolean clipViews = this.clipViews;
		
		if (clipViews) {
			context.limitRenderingBounds(this.renderingBounds);
		}
		
		this.applyTransformToContext(context);
		
		float oldTint = context.getTint();
		
		float currentAlpha = parentAlpha * this.getAlpha();
		
		tmpColor.set(this.tint.r, this.tint.g, this.tint.b, currentAlpha);
		context.setTint(tmpColor.toFloatBits());
		
		if (this.background != null) {
			this.background.draw(context, 0, 0, this.frame.width, this.frame.height, currentAlpha);
		}
		
		this.drawInRect(context, 0, 0, this.frame.width, this.frame.height, currentAlpha);

		View[] views = this.views.begin();
		for (int i = 0, length = this.views.size; i < length; i++) {
			final View view = views[i];
			view.draw(context, currentAlpha);
		}
		this.views.end();
		
		if (clipViews) {
			context.unlimitRenderingBounds();
		}
		
		this.restoreTransformToContext(context);
		
		context.setTint(oldTint);
		
		this.shouldBeDrawn = false;
		
		return true;
	}
	
	public void update(float delta) {
		if (this.isVisible()) {
			this.shouldBeDrawn = true;
		}
		
		View[] subViews = this.views.begin();
		
		for (int i = 0, length = this.views.size; i < length; i++) {
			subViews[i].update(delta);
		}
		
		this.views.end();
	}
	
	public void addView(View view) {
		if (view == null) {
			throw new IllegalArgumentException("view may not be null");
		}
		
		view.removeFromParentView();
		view.makeDirty();
		view.parentView = this;
		
		this.views.add(view);
	}
	
	public boolean removeView(View view) {
		if (view == null) {
			throw new IllegalArgumentException("view may not be null");
		}
		
		if (this.views.removeValue(view, true)) {
			view.parentView = null;
			return true;
		}
		
		return false;
	}
	
	public boolean removeFromParentView() {
		if (this.parentView != null) {
			return this.parentView.removeView(this);
		} else {
			return false;
		}
	}
	
	public void makeDirty() {
		this.transformMatrixDirty = true;
	}
	
	public void moveToCenterOf(Rectangle rectangle) {
		this.frame.setCenter(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
		this.transformMatrixDirty = true;
	}
	
	/**
	 * Offset the view position
	 * @param x
	 * @param y
	 */
	public void translate(float x, float y) {
		this.setPosition(this.frame.x + x, this.frame.y);
	}
	
	final public void setOriginToCenter() {
		this.originType = ORIGIN_CENTER;
		this.transformMatrixDirty = true;
	}
	
	final public void setOriginToBottomLeft() {
		this.originType = ORIGIN_BOTTOM_LEFT;
		this.transformMatrixDirty = true;
	}
	
	final public void setOriginToBottomRight() {
		this.originType = ORIGIN_BOTTOM_RIGHT;
		this.transformMatrixDirty = true;
	}
	
	final public void setOriginToTopLeft() {
		this.originType = ORIGIN_TOP_LEFT;
		this.transformMatrixDirty = true;
	}
	
	final public void setOriginToTopRight() {
		this.originType = ORIGIN_TOP_RIGHT;
		this.transformMatrixDirty = true;
	}
	
	final public void setOriginToCustom() {
		this.originType = ORIGIN_CUSTOM;
		this.transformMatrixDirty = true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	final public int getOriginType() {
		return this.originType;
	}
	
	final public void setOriginType(int originType) {
		this.originType = originType;
		this.transformMatrixDirty = true;
	}
	
	/**
	 * Get the View frame. Modifying the returned rectangle will have no effect
	 * until makeDirty() is called on the view. If you want to safely change the view
	 * position,
	 * @return the view Frame
	 */
	public Rectangle getFrame() {
		return frame;
	}
	
	public void setX(float x) {
		this.frame.x = x;
		this.transformMatrixDirty = true;
	}
	
	public void setY(float y) {
		this.frame.y = y;
		this.transformMatrixDirty = true;
	}
	
	public void setWidth(float width) {
		this.frame.width = width;
		this.transformMatrixDirty = true;
	}
	
	public void setHeight(float height) {
		this.frame.height = height;
		this.transformMatrixDirty = true;
	}
	
	public void setPosition(float x, float y) {
		this.frame.setPosition(x, y);
		this.transformMatrixDirty = true;
	}
	
	public void setSize(float width, float height) {
		this.frame.setSize(width, height);
		this.transformMatrixDirty = true;
	}
	
	public void setFrame(Rectangle frame) {
		if (frame != null) {
			this.setFrame(frame.x, frame.y, frame.width, frame.height);
		} else {
			this.setFrame(0, 0, 0, 0);
		}
	}
	
	public void setFrame(float x, float y, float width, float height) {
		this.frame.set(x, y, width, height);
		this.transformMatrixDirty = true;
	}

	public Drawable getBackground() {
		return background;
	}

	/**
	 * Set the background that will be drawn first
	 * @param background
	 */
	public void setBackground(Drawable background) {
		this.background = background;
	}

	/**
	 * @return the color tint applied to the view
	 */
	public Color getTint() {
		return tint;
	}

	/**
	 *  Set a color tint to apply to the view
	 * @param the new tint
	 */
	public void setTint(Color tint) {
		this.tint.set(tint);
	}
	
	public float getAlpha() {
		return this.tint.a;
	}
	
	/**
	 * Set the view alpha
	 * @param alpha
	 */
	public void setAlpha(float alpha) {
		this.tint.a = alpha;
		this.setTint(this.tint);
	}
	
	/**
	 * @return true if the view is not hidden and alpha is > 0
	 */
	public boolean isVisible() {
		return !this.hidden && this.tint.a > 0;
	}

	/**
	 * 
	 * @return true if the view is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * If true, the view will not be drawn and it wont receive any input
	 * @param hidden
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public View getParentView() {
		return this.parentView;
	}

	public boolean isClipsViews() {
		return clipViews;
	}

	/**
	 * If true, the views will be clipped if they are not entirely inside this view
	 * @param clipsViews
	 */
	public void setClipsViews(boolean clipsViews) {
		this.clipViews = clipsViews;
	}

	public Projection getProjection() {
		return projection;
	}

	/**
	 * If not null, while drawing the view will be projected in the specified projection
	 * If null, the view while use the parent projection
	 * By default views are projected using the screen size
	 * @param projection
	 */
	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.transformMatrixDirty = true;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
		this.transformMatrixDirty = true;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
		this.transformMatrixDirty = true;
	}
	
	public void setScaleXY(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.transformMatrixDirty = true;
	}
	
	public void setScaleXY(float scaleXY) {
		this.scaleX = scaleXY;
		this.scaleY = scaleXY;
		this.transformMatrixDirty = true;
	}
	
	public float getOriginX() {
		return originX;
	}

	/**
	 * Change the point X to which the view is scaled or rotated
	 * Using this method will automatically set the origin type to CUSTOM
	 * @param originX
	 */
	public void setOriginX(float originX) {
		this.originX = originX;
		this.setOriginToCustom();
	}

	public float getOriginY() {
		return originY;
	}

	/**
	 * Change the point Y to which the view is scaled or rotated
	 * Using this method will automatically set the origin type to CUSTOM
	 * @param originY
	 */
	public void setOriginY(float originY) {
		this.originY = originY;
		this.setOriginToCustom();
	}
}
