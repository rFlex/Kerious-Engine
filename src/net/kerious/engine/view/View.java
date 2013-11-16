/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// View.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 9:45:39 AM
////////

package net.kerious.engine.view;

import me.corsin.javatools.misc.PoolableImpl;
import net.kerious.engine.animations.Animation;
import net.kerious.engine.animations.ChangeFrameAnimation;
import net.kerious.engine.drawable.Drawable;
import net.kerious.engine.input.TouchResponder;
import net.kerious.engine.renderer.DrawingContext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.SnapshotArray;

public class View extends PoolableImpl implements TouchResponder {
	
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
	final private Color tint;
	final private Rectangle screenFrame;
	
	final private Matrix3 localTransformMatrix;
	final private Matrix3 drawingTransformMatrix;
	final private Matrix4 contextTransformMatrix;
	final private Matrix4 savedTransformMatrix;
	
	final private Rectangle frame;
	final private Rectangle renderingFrame;
	
	private SnapshotArray<Animation> animations;
	private Drawable background;
	private View parentView;
	private float rotation;
	private float projectionRatioX;
	private float projectionRatioY;
	private float originX;
	private float originY;
	private float parentProjectionRatioX;
	private float parentProjectionRatioY;
	private float parentX;
	private float parentY;
	private boolean hidden;
	private boolean clipViews;
	private boolean willBeDrawn;
	private boolean transformMatrixDirty;
	private boolean wasDrawn;
	private boolean touchEnabled;
	private int originType;
	
	// Animations
	private static boolean animationStarted;
	private static float animateTime;
	private static int animationSequence;
	private static Interpolation animationInterpolation;
	private static Runnable animationCompletionHandler;
	
	private ChangeFrameAnimation changedFrameAnimation;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public View() {
		this.views = new SnapshotArray<View>(true, 4, View.class);
		this.screenFrame = new Rectangle();
		this.frame = new Rectangle();
		this.renderingFrame = new Rectangle();
		this.tint = new Color(Color.WHITE);
		this.setTint(this.tint);
		this.localTransformMatrix = new Matrix3();
		this.drawingTransformMatrix = new Matrix3();
		this.contextTransformMatrix = new Matrix4();
		this.savedTransformMatrix = new Matrix4();
		this.animations = new SnapshotArray<Animation>(false, 4, Animation.class);
		this.projectionRatioX = 1;
		this.projectionRatioY = 1;
		this.originType = ORIGIN_BOTTOM_LEFT;
		this.touchEnabled = true;
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Inherit class should override this to implement the drawing
	 * @param batch
	 * @param width
	 * @param height
	 */
	public void drawView(DrawingContext context, float width, float height, float alpha) {
		
	}
	
	final private Matrix3 computeTransformMatrix(float renderingFrameX, float renderingFrameY,
			float renderingFrameWidth, float renderingFrameHeight) {
		if (this.transformMatrixDirty) {
			boolean originChanged = this.originType != ORIGIN_BOTTOM_LEFT;
			float transformOriginX = 0;
			float transformOriginY = 0;
			
			if (originChanged) {
				switch (this.originType) {
				case ORIGIN_BOTTOM_RIGHT:
					transformOriginX = renderingFrameWidth;
					break;
				case ORIGIN_TOP_LEFT:
					transformOriginY = renderingFrameHeight;
					break;
				case ORIGIN_TOP_RIGHT:
					transformOriginX = renderingFrameWidth;
					transformOriginY = renderingFrameHeight;
					break;
				case ORIGIN_CENTER:
					transformOriginX = renderingFrameWidth * 0.5f;
					transformOriginY = renderingFrameHeight * 0.5f;
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
			
			if (this.projectionRatioX != 1 || this.projectionRatioY != 1) {
				this.localTransformMatrix.scale(this.projectionRatioX, this.projectionRatioY);
			}
			
			if (originChanged) {
				this.localTransformMatrix.translate(-transformOriginX, -transformOriginY);
			}
			
			this.localTransformMatrix.trn(renderingFrameX, renderingFrameY);
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
	
	final private void applyTransformToContext(DrawingContext context, float renderingFrameX, float renderingFrameY,
			float renderingFrameWidth, float renderingFrameHeight) {
		Matrix3 transformMatrix = this.computeTransformMatrix(renderingFrameX, renderingFrameY,
				renderingFrameWidth, renderingFrameHeight);
		
		this.savedTransformMatrix.set(context.getTransformMatrix());
		
		this.contextTransformMatrix.set(transformMatrix);
		context.setTransformMatrix(this.contextTransformMatrix);
	}
	
	final private void restoreTransformToContext(DrawingContext context) {
		context.setTransformMatrix(this.savedTransformMatrix);
	}
	
	final public boolean draw(DrawingContext context, float currentScreenX, float currentScreenY,
			float currentProjectionRatioX, float currentProjectionRatioY, float parentAlpha) {
		if (!this.willBeDrawn) {
			this.wasDrawn = false;
			return false;
		}
		
		float renderingFrameX = this.renderingFrame.x;
		float renderingFrameY = this.renderingFrame.y;
		float renderingFrameWidth = this.renderingFrame.width;
		float renderingFrameHeight = this.renderingFrame.height;
		
		this.parentProjectionRatioX = currentProjectionRatioX;
		this.parentProjectionRatioY = currentProjectionRatioY;
		this.parentX = currentScreenX;
		this.parentY = currentScreenY;

		currentScreenX += renderingFrameX * currentProjectionRatioX;
		currentScreenY += renderingFrameY * currentProjectionRatioY;
		
		this.screenFrame.set(currentScreenX, currentScreenY, renderingFrameWidth * currentProjectionRatioX, renderingFrameHeight * currentProjectionRatioY);
		
		if (!context.isVisibleInContext(this.screenFrame)) {
			this.wasDrawn = false;
			return false;
		}

		final boolean clipViews = this.clipViews;
		
		if (clipViews) {
			context.limitRenderingBounds(this.screenFrame);
		}
		
		this.applyTransformToContext(context, renderingFrameX, renderingFrameY, renderingFrameWidth, renderingFrameHeight);
		
		float oldTint = context.getTint();
		
		float currentAlpha = parentAlpha * this.getAlpha();
		
		tmpColor.set(this.tint.r, this.tint.g, this.tint.b, currentAlpha);
		context.setTint(tmpColor.toFloatBits());
		
		if (this.background != null) {
			this.background.draw(context, 0, 0, renderingFrameWidth, renderingFrameHeight, currentAlpha);
		}
		
		this.drawView(context, renderingFrameWidth, renderingFrameHeight, currentAlpha);
		
		currentProjectionRatioX *= this.projectionRatioX;
		currentProjectionRatioY *= this.projectionRatioY;

		View[] views = this.views.begin();
		for (int i = 0, length = this.views.size; i < length; i++) {
			final View view = views[i];
			view.draw(context, currentScreenX, currentScreenY, currentProjectionRatioX, currentProjectionRatioY, currentAlpha);
		}
		this.views.end();
		
		if (clipViews) {
			context.unlimitRenderingBounds();
		}
		
		this.restoreTransformToContext(context);
		
		context.setTint(oldTint);
		
		this.willBeDrawn = false;
		this.wasDrawn = true;
		
		return true;
	}
	
	public void update(float deltaTime) {
		if (this.isVisible()) {
			this.willBeDrawn = true;
		}
		
		Animation[] animations = this.animations.begin();
		for (int i = 0, length = this.animations.size; i < length; i++) {
			final Animation animation = animations[i];
			
			if (!animation.hasExpired()) {
				animation.update(deltaTime);
			}
			
			if (animation.hasExpired()) {
				this.animations.removeValue(animation, true);
				
				if (animation == this.changedFrameAnimation) {
					this.changedFrameAnimation = null;
				}
				
				animation.removedFromView();
				animation.release();
			}
		}
		this.animations.end();
		
		View[] subViews = this.views.begin();
		
		for (int i = 0, length = this.views.size; i < length; i++) {
			subViews[i].update(deltaTime);
		}
		
		this.views.end();
	}
	
	public void addAnimation(Animation animation) {
		if (animation == null) {
			throw new IllegalArgumentException("animation may not be null");
		}
		
		animation.setView(this);
		
		this.animations.add(animation);
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
	
	public Vector2 getScreenPosition(Vector2 outputVector) {
		return this.getPositionForViewCoordinate(null, outputVector);
	}
	
	public Vector2 getPositionForViewCoordinate(View otherView, Vector2 outputVector) {
		outputVector.x = 0;
		outputVector.y = 0;

		View currentView = this;
		while (currentView != null && currentView != otherView) {
			float x = currentView.frame.x;
			float y = currentView.frame.y;
			
			if (this.projectionRatioX != 1 || this.projectionRatioY != 1) {
				x *= this.projectionRatioX;
				y *= this.projectionRatioY;
			}
			
			outputVector.x += x;
			outputVector.y += y;
		}
		
		return outputVector;
	}
	
	@Override
	public TouchResponder getBestTouchResponderForLocation(float x, float y) {
		if (!this.screenFrame.contains(x, y)) {
			return null;
		}
		
		TouchResponder bestResponder = null;
		
		final View[] views = this.views.items;
		for (int i = this.views.size - 1; bestResponder == null &&i >= 0; i--) {
			final View currentView = views[i];
			
			if (currentView.isAvailableForTouchResponding()) {
				bestResponder = currentView.getBestTouchResponderForLocation(x, y);
			}
		}
		
		if (bestResponder == null) {
			bestResponder = this;
		}
		
		return bestResponder;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": {" + this.frame + "}";
	}
	
	@Override
	public Vector2 convertScreenToTouchResponderLocation(float x, float y, Vector2 output) {
		float realX = x - this.parentX;
		float realY = y - this.parentY;
		
		output.x = realX / this.parentProjectionRatioX;
		output.y = realY / this.parentProjectionRatioY;
		
		return output;
	}

	@Override
	public void onTouchDown(int pointer, float x, float y, int button) {
		
	}

	@Override
	public void onTouchUp(int pointer, float x, float y, int button) {
		
	}

	@Override
	public void onTouchDragged(int pointer, float x, float y, int button) {
		
	}
	
	@Override
	public void onTouchOver(float x, float y) {
		
	}
	
	public void makeDirty() {
		this.transformMatrixDirty = true;
	}
	
	public void moveToCenterOf(Rectangle rectangle) {
		this.frame.setCenter(rectangle.x + rectangle.width * 0.5f, rectangle.y + rectangle.height * 0.5f);
		this.transformMatrixDirty = true;
	}
	
	public static void beginAnimation(float time) {
		beginAnimation(time, null, null);
	}
	
	public static void beginAnimation(float time, Interpolation interpolation) {
		beginAnimation(time, interpolation, null);
	}
	
	public static void beginAnimation(float time, Interpolation interpolation, Runnable completionHandler) {
		if (interpolation == null) {
			interpolation = Interpolation.linear;
		}
		
		animationStarted = true;
		animationInterpolation = interpolation;
		animationCompletionHandler = completionHandler;
		animateTime = time;
	}
	
	public static void endAnimation() {
		animationStarted = false;
		animateTime = 0;
		animationInterpolation = null;
		animationSequence++;
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
	
	final private void removeFrameAnimation() {
		if (this.changedFrameAnimation != null && this.changedFrameAnimation.getAnimationSequence() != animationSequence) {
			// The changed frame is part from an old animation, removing it
			this.changedFrameAnimation.setExpired(true);
			this.changedFrameAnimation = null;
		}
	}
	
	final private void willChangeFrame() {
		this.removeFrameAnimation();

		if (animationStarted) {
			if (this.changedFrameAnimation == null) {
				this.changedFrameAnimation = ChangeFrameAnimation.create();
				this.changedFrameAnimation.setAnimationSequence(animationSequence);
				this.changedFrameAnimation.setDuration(animateTime);
				this.changedFrameAnimation.setInterpolation(animationInterpolation);
				this.changedFrameAnimation.setEndedCallback(animationCompletionHandler);
				
				this.addAnimation(this.changedFrameAnimation);
			}
		}
	}
	
	final private void didChangeFrame() {
		this.transformMatrixDirty = true;
		
		if (!animationStarted) {
			this.renderingFrame.set(this.frame);
		} else {
			this.changedFrameAnimation.setEndFrame(this.frame);
		}
	}
	
	final private void willChangeColor() {
		// TODO Implement color animation
	}
	
	final private void didChangeColor() {
		// TODO Implement color animation
	}
	
	
	final private void willChangeRotation() {
		// TODO Implement rotation animation
	}
	
	final private void didChangeRotation() {
		// TODO Implement rotation animation
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
	 * position, use setFrame
	 * @return the view Frame
	 */
	public Rectangle getFrame() {
		return frame;
	}
	
	public void setX(float x) {
		this.setFrame(x, this.frame.y, this.frame.width, this.frame.height);
	}
	
	public float getX() {
		return this.frame.x;
	}
	
	public void setY(float y) {
		this.setFrame(this.frame.x, y, this.frame.width, this.frame.height);
	}
	
	public float getY() {
		return this.frame.y;
	}
	
	public void setWidth(float width) {
		this.setFrame(this.frame.x, this.frame.y, width, this.frame.height);
	}
	
	public float getWidth() {
		return this.frame.width;
	}
	
	public void setHeight(float height) {
		this.setFrame(this.frame.x, this.frame.y, this.frame.width, height);
	}
	
	public float getHeight() {
		return this.frame.height;
	}
	
	public void setPosition(float x, float y) {
		this.setFrame(x, y, this.frame.width, this.frame.height);
	}
	
	public void setSize(float width, float height) {
		this.setFrame(this.frame.x, this.frame.y, width, height);
	}
	
	public void setFrame(Rectangle frame) {
		if (frame != null) {
			this.setFrame(frame.x, frame.y, frame.width, frame.height);
		} else {
			this.setFrame(0, 0, 0, 0);
		}
	}
	
	public void setFrame(float x, float y, float width, float height) {
		this.willChangeFrame();
		this.frame.set(x, y, width, height);
		this.didChangeFrame();
	}
	
	public Rectangle getRenderingFrame() {
		return this.renderingFrame;
	}
	
	public void setRenderingFrame(float x, float y, float width, float height) {
		this.renderingFrame.set(x, y, width, height);
		this.transformMatrixDirty = true;
	}
	
	public void setRenderingFrame(Rectangle frame) {
		this.renderingFrame.set(frame);
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
		this.willChangeColor();
		this.tint.set(tint);
		this.didChangeColor();
	}
	
	public float getAlpha() {
		return this.tint.a;
	}
	
	/**
	 * Set the view alpha
	 * @param alpha
	 */
	public void setAlpha(float alpha) {
		this.willChangeColor();
		this.tint.a = alpha;
		this.didChangeColor();
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

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.willChangeRotation();
		this.rotation = rotation;
		this.didChangeRotation();
	}

	public float getProjectionRatioX() {
		return projectionRatioX;
	}

	public void setProjectionRatioX(float projectionRatioX) {
		this.projectionRatioX = projectionRatioX;
		this.transformMatrixDirty = true;
	}

	public float getProjectionRatioY() {
		return projectionRatioY;
	}

	public void setProjectionRatioY(float projectionRatioY) {
		this.projectionRatioY = projectionRatioY;
		this.transformMatrixDirty = true;
	}
	
	public void setProjectionRatioXY(float projectionRatioX, float projectionRatioY) {
		this.projectionRatioX = projectionRatioX;
		this.projectionRatioY = projectionRatioY;
		this.transformMatrixDirty = true;
	}
	
	public void setProjectionRatioXY(float projectionRatioXY) {
		this.projectionRatioX = projectionRatioXY;
		this.projectionRatioY = projectionRatioXY;
		this.transformMatrixDirty = true;
	}
	
	public float getOriginX() {
		switch (this.originType) {
		case ORIGIN_BOTTOM_RIGHT:
		case ORIGIN_TOP_RIGHT:
			return this.frame.width;
		case ORIGIN_CENTER:
			return this.frame.width * 0.5f;
		case ORIGIN_CUSTOM:
			return this.originX;
		}
		return 0;
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
		switch (this.originType) {
		case ORIGIN_TOP_LEFT:
		case ORIGIN_TOP_RIGHT:
			return this.frame.height;
		case ORIGIN_CENTER:
			return this.frame.height * 0.5f;
		case ORIGIN_CUSTOM:
			return this.originY;
		}
		return 0;
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

	public boolean isTouchEnabled() {
		return touchEnabled;
	}
	
	public boolean willBeDrawn() {
		return this.willBeDrawn;
	}
	
	public boolean wasDrawn() {
		return this.wasDrawn;
	}

	public void setTouchEnabled(boolean touchEnabled) {
		this.touchEnabled = touchEnabled;
	}

	@Override
	public boolean isAvailableForTouchResponding() {
		return this.wasDrawn && this.touchEnabled;
	}
}
