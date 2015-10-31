/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.animations
// ChangeFrameAnimation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 11, 2013 at 6:09:07 PM
////////

package net.kerious.engine.animations;

import net.kerious.engine.view.KView;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class ChangeFrameAnimation extends TemporalAnimation {

	////////////////////////
	// VARIABLES
	////////////////
	
	private static Pool<ChangeFrameAnimation> pool = new Pool<ChangeFrameAnimation>() {
		@Override
		protected ChangeFrameAnimation newObject() {
			return new ChangeFrameAnimation();
		}
	};
	
	private Rectangle startFrame;
	private Rectangle endFrame;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ChangeFrameAnimation() {
		this.startFrame = new Rectangle();
		this.endFrame = new Rectangle();
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void applyToView(KView view, float currentRatio) {
		float currentX = this.startFrame.x + (this.endFrame.x - this.startFrame.x) * currentRatio;
		float currentY = this.startFrame.y + (this.endFrame.y - this.startFrame.y) * currentRatio;
		float currentWidth = this.startFrame.width + (this.endFrame.width - this.startFrame.width) * currentRatio;
		float currentHeight = this.startFrame.height + (this.endFrame.height - this.startFrame.height) * currentRatio;

		view.setRenderingFrame(currentX, currentY, currentWidth, currentHeight);
	}

	@Override
	protected void setInitialStateForView(KView view) {
		this.startFrame.set(view.getFrame());
	}
	
	public static ChangeFrameAnimation create() {
		ChangeFrameAnimation animation = pool.obtain();
		
		animation.setPool(pool);
		
		return animation;
	}
		

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Rectangle getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(Rectangle endFrame) {
		this.endFrame.set(endFrame);
	}
}
