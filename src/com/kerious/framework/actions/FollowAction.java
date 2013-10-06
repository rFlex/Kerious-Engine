/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.actions
// FollowAction.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 18, 2012 at 12:59:57 PM
////////

package com.kerious.framework.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class FollowAction extends Action {

	////////////////////////
	// VARIABLES
	////////////////

	private Actor toFollowActor;
	private Vector2 offset;
	private boolean stopped;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public FollowAction() {
		this(null, null);
	}
	
	public FollowAction(Actor toFollow) {
		this(toFollow, null);
	}
	
	public FollowAction(Actor toFollow, Vector2 offset) {
		this.offset = new Vector2();

		this.stopped = false;
		
		this.follow(toFollow, offset);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public boolean act(float delta) {
		if (this.toFollowActor != null && this.actor != null) {
			this.actor.setPosition(this.toFollowActor.getX() + this.offset.x, this.toFollowActor.getY() + this.offset.y);
		}
		
		return this.stopped;
	}
	
	public void follow(Actor toFollow) {
		this.follow(toFollow, null);
	}
	
	public void follow(Actor toFollow, Vector2 offset) {
		this.toFollowActor = toFollow;
		
		if (offset != null) {
			this.offset.set(offset);
		}
	}
	
	public void stop() {
		this.stopped = true;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
