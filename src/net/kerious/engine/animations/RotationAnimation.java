/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.animations
// RotationAnimation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 1:38:48 PM
////////

package net.kerious.engine.animations;

import net.kerious.engine.view.View;

import com.badlogic.gdx.utils.Pool;

public class RotationAnimation extends TemporalAnimation {

	
	////////////////////////
	// VARIABLES
	////////////////
	
	private static Pool<RotationAnimation> pool = new Pool<RotationAnimation>() {
		@Override
		protected RotationAnimation newObject() {
			return new RotationAnimation();
		}
	};
	
	private float startRotation;
	private float endRotation;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public RotationAnimation() {
		
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	protected void applyToView(View view, float currentRatio) {
		float currentRotation = this.startRotation + (this.endRotation - this.startRotation) * currentRatio;
		
		view.setRenderingRotation(currentRotation);
	}

	@Override
	protected void setInitialStateForView(View view) {
		this.startRotation = view.getRotation();
	}
	
	public static RotationAnimation create() {
		RotationAnimation ra = pool.obtain();
		
		ra.setPool(pool);
		
		return ra;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public void setEndRotation(float rotation) {
		this.endRotation = rotation;
	}
	
	public float getEndRotation() {
		return this.endRotation;
	}
}
