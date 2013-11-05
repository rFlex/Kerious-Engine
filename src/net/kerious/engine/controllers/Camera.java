/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.controllers
// Camera.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 11:39:12 AM
////////

package net.kerious.engine.controllers;

import net.kerious.engine.renderer.DrawingContext;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class Camera extends Rectangle {

	////////////////////////
	// VARIABLES
	////////////////
	
	private static final long serialVersionUID = -8907087048858960637L;
	
	private OrthographicCamera internalCamera;
//	private Rectangle tmp;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Camera(float viewportWidth, float viewportHeight) {
		this();
		this.setSize(viewportWidth, viewportHeight);
	}
	
	public Camera() {
//		this.tmp = new Rectangle();
		this.internalCamera = new OrthographicCamera();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void applyToContext(DrawingContext context) {
		this.internalCamera.viewportWidth = this.width;
		this.internalCamera.viewportHeight = this.height;
		this.internalCamera.update();
		
		context.getBatch().setProjectionMatrix(this.internalCamera.combined);
	}
	
	public void limitRenderingBounds(DrawingContext context, float x, float y, float width, float height, Rectangle outputScissors) {
//		this.tmp.set(x, y, width, height);
		outputScissors.set(x, y, width, height);
		
		ScissorStack.pushScissors(outputScissors);
	}
	
	public void unlimitRenderingBounds() {
		ScissorStack.popScissors();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public float getZoom() {
		return this.internalCamera.zoom;
	}
	
	public void setZoom(float zoom) {
		this.internalCamera.zoom = zoom;
	}

}
