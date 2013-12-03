/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// NullRenderer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:20:04 PM
////////

package net.kerious.engine.renderer;

import net.kerious.engine.view.View;

public class NullRenderer implements Renderer {

	////////////////////////
	// VARIABLES
	////////////////
	
	private float windowWidth;
	private float windowHeight;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NullRenderer() {
		
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void render(View view) {
		
	}
	
	@Override
	public void initialize() {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public boolean isDisposed() {
		return false;
	}

	@Override
	public float getWindowWidth() {
		return this.windowWidth;
	}

	@Override
	public float getWindowHeight() {
		return this.windowHeight;
	}

	@Override
	public DrawingContext getContext() {
		return null;
	}

	@Override
	public boolean isDrawingContextAvailable() {
		return false;
	}

	@Override
	public void setWindowSize(float width, float height) {
		this.windowWidth = width;
		this.windowHeight = height;
	}
}
