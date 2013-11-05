/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// NullRenderer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:20:04 PM
////////

package net.kerious.engine.renderer;

import net.kerious.engine.controllers.ViewController;

public class NullRenderer implements Renderer {

	////////////////////////
	// VARIABLES
	////////////////
	
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
	public void render(ViewController viewController) {
		
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
		return 0;
	}

	@Override
	public float getWindowHeight() {
		return 0;
	}
}
