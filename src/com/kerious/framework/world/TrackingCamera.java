/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world
// TrackingCamera.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 11, 2012 at 4:54:43 PM
////////

package com.kerious.framework.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TrackingCamera extends OrthographicCamera {

	////////////////////////
	// VARIABLES
	////////////////

	final private Rectangle frame;
	private Actor followed;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TrackingCamera(float width, float height) {
		super(width , height);
		
		this.frame = new Rectangle();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void stopFollowing() {
		this.follow(null);
	}
	
	public void follow(Actor actor) { 
		this.followed = actor; 
	}
	
	@Override
	public void update(boolean updateFrustum) {
		if (this.followed != null) {
			this.position.x = followed.getX(); 
			this.position.y = followed.getY();
		}
		
		super.update(updateFrustum);

		if (this.frame != null) {
			this.frame.x = this.position.x - this.viewportWidth / 2;
			this.frame.y = this.position.y - this.viewportHeight / 2;
			this.frame.width = this.viewportWidth;
			this.frame.height = this.viewportHeight;
		}
		
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final Rectangle getFrame() {
		return frame;
	}
	
	public final Actor getFollowed() {
		return this.followed;
	}
 
}
