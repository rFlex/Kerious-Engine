/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngineNullDrawingImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:19:52 PM
////////

package net.kerious.engine;

import net.kerious.engine.renderer.NullRenderer;

public class KeriousEngineNullDrawing extends KeriousEngine {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousEngineNullDrawing(KeriousEngineListener listener) {
		super(new NullRenderer(), listener);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
