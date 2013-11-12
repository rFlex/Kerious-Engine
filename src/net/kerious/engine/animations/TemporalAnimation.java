/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.animations
// TemporalAnimation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 11, 2013 at 6:09:56 PM
////////

package net.kerious.engine.animations;

import com.badlogic.gdx.math.Interpolation;

import net.kerious.engine.view.View;

public abstract class TemporalAnimation extends Animation {

	////////////////////////
	// VARIABLES
	////////////////
	
	private float currentTime;
	private float duration;
	private float currentRatio;
	private boolean instant;
	private Interpolation interpolation;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	abstract protected void applyToView(View view, float currentRatio);
	
	public void update(float deltaTime) {
		this.currentTime += deltaTime;
		
		if (this.currentTime >= this.duration) {
			this.currentTime = this.duration;
			this.setExpired(true);
		}

		if (this.instant) {
			this.currentRatio = 1;
		} else {
			this.currentRatio = this.interpolation.apply(this.currentTime / this.duration);
		}
		
		this.applyToView(this.getView(), this.currentRatio);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.currentTime = 0;
		this.duration = 0;
		this.instant = true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
		this.instant = duration == 0;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public float getCurrentRatio() {
		return currentRatio;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}
}
