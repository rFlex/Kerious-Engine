/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.animations
// Animation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 11, 2013 at 5:51:00 PM
////////

package net.kerious.engine.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.view.KView;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class Animation implements TemporaryUpdatable, Poolable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Pool pool;
	private KView view;
	private int animationSequence;
	private boolean expired;
	private Runnable endedCallback;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Animation() {
		this.reset();
	}

	////////////////////////
	// METHODS
	////////////////

	public void release() {
		if (this.pool != null) {
			this.pool.free(this);
			this.pool = null;
		}
	}
	
	public void reset() {
		this.expired = false;
		this.endedCallback = null;
	}
	
	public void removedFromView() {
		if (this.endedCallback != null) {
			this.endedCallback.run();
		}
	}
	
	abstract protected void setInitialStateForView(KView view);
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KView getView() {
		return view;
	}

	public void setView(KView view) {
		this.view = view;
		
		if (view != null) {
			this.setInitialStateForView(view);
		}
	}

	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}
	
	public boolean hasExpired() {
		return this.expired;
	}
	
	public void setExpired(boolean value) {
		this.expired = value;
	}

	public int getAnimationSequence() {
		return animationSequence;
	}

	public void setAnimationSequence(int animationSequence) {
		this.animationSequence = animationSequence;
	}

	public Runnable getEndedCallback() {
		return endedCallback;
	}

	public void setEndedCallback(Runnable endedCallback) {
		this.endedCallback = endedCallback;
	}

}
