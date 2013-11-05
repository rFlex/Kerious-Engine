/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// View.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 9:45:39 AM
////////

package net.kerious.engine.view;

import net.kerious.engine.controllers.Camera;
import net.kerious.engine.drawable.Drawable;
import net.kerious.engine.renderer.DrawingContext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.SnapshotArray;

public class View {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private static Color tmpColor = new Color();
	
	final private SnapshotArray<View> views;
	final private Rectangle frame;
	final private Color tint;
	private Rectangle renderingBounds;
	private Rectangle scissors;
	private boolean hidden;
	private boolean clipViews;
	private Drawable background;
	private View parentView;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public View() {
		this.views = new SnapshotArray<View>(true, 4, View.class);
		this.renderingBounds = new Rectangle();
		this.scissors = new Rectangle();
		this.frame = new Rectangle();
		this.tint = new Color(Color.WHITE);
		this.setTint(this.tint);
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
	public void drawInRect(DrawingContext context, float x, float y, float width, float height) {
		
	}
	
	final public boolean draw(Camera camera, DrawingContext context, float x, float y, float parentAlpha) {
		if (!this.isVisible()) {
			return false;
		}
		
		this.renderingBounds.set(x, y, this.frame.width, this.frame.height);
		
		if (!camera.overlaps(this.renderingBounds)) {
			return false;
		}
		
		final SpriteBatch batch = context.getBatch();
		
		boolean clipViews = this.clipViews;
		
		if (clipViews) {
			camera.limitRenderingBounds(context, x, y, this.frame.width, this.frame.height, this.scissors);
		}
		
		float oldColor = batch.getColor().toFloatBits();
		
		float currentAlpha = parentAlpha * this.getAlpha();
		
		tmpColor.set(this.tint.r, this.tint.g, this.tint.b, currentAlpha);
		batch.setColor(tmpColor);
		
		if (this.background != null) {
			this.background.draw(context, x, y, this.frame.width, this.frame.height);
		}
		
		this.drawInRect(context, x, y, this.frame.width, this.frame.height);

		View[] views = this.views.begin();
		for (int i = 0, length = this.views.size; i < length; i++) {
			final View view = views[i];
			view.draw(camera, context, x + view.frame.x, y + view.frame.y, currentAlpha);
		}
		this.views.end();
		
		if (clipViews) {
			camera.unlimitRenderingBounds();
		}
		
		batch.setColor(oldColor);
		
		return true;
	}
	
	public void act(float delta) {
		View[] subViews = this.views.begin();
		
		for (int i = 0, length = this.views.size; i < length; i++) {
			subViews[i].act(delta);
		}
		
		this.views.end();
	}
	
	public void addView(View view) {
		if (view == null) {
			throw new IllegalArgumentException("view may not be null");
		}
		
		view.removeFromParentView();
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

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	/**
	 * Get the View frame. Do not modify the value directly from the returned rectangle.
	 * @return the view Frame
	 */
	public Rectangle getFrame() {
		return frame;
	}
	
	public void setX(float x) {
		this.setFrame(x, this.frame.y, this.frame.width, this.frame.height);
	}
	
	public void setY(float y) {
		this.setFrame(this.frame.x, y, this.frame.width, this.frame.height);
	}
	
	public void setWidth(float width) {
		this.setFrame(this.frame.x, this.frame.y, width, this.frame.height);
	}
	
	public void setHeight(float height) {
		this.setFrame(this.frame.x, this.frame.y, this.frame.width, height);
	}
	
	public void setFrame(float x, float y, float width, float height) {
		this.frame.set(x, y, width, height);
	}

	public void setFrame(Rectangle frame) {
		if (frame != null) {
			this.setFrame(frame.x, frame.y, frame.width, frame.height);
		} else {
			this.setFrame(0, 0, 0, 0);
		}
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
	 * If true, the view will not be drawn and the input will not work
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
}
