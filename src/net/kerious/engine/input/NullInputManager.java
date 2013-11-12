/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.input
// NullInputManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2013 at 12:50:55 AM
////////

package net.kerious.engine.input;

public class NullInputManager implements InputManager {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void initialize() {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public KeyboardResponder getKeyboardResponder() {
		return null;
	}

	@Override
	public void setKeyboardResponder(KeyboardResponder keyboardResponder) {
		
	}

	@Override
	public void setTouchResponder(TouchResponder touchResponder) {
		
	}

	@Override
	public TouchResponder getTouchResponder() {
		return null;
	}
}
