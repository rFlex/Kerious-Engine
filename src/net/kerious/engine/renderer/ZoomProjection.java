/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// ZoomProjection.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 6, 2013 at 4:06:49 PM
////////

package net.kerious.engine.renderer;

import com.badlogic.gdx.Gdx;

public class ZoomProjection extends Projection {

	private static final long serialVersionUID = 6344141534727587480L;

	////////////////////////
	// VARIABLES
	////////////////
	
	private float zoom;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ZoomProjection() {
		this.setZoom(1);
	}
	
	public ZoomProjection(float zoom) {
		this.setZoom(zoom);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void setZoom(float zoom) {
		this.width = Gdx.graphics.getWidth() / zoom;
		this.height = Gdx.graphics.getHeight() / zoom;
		
		this.zoom = zoom;
	}
}
