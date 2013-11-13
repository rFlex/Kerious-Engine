/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.animations
// RotationAnimation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 1:38:48 PM
////////

package net.kerious.engine.animations;

import com.badlogic.gdx.utils.Pool;

import net.kerious.engine.view.View;

@SuppressWarnings("unused")
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
}
