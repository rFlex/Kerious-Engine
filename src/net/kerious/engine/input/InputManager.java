/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.input
// InputManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2013 at 12:47:22 AM
////////

package net.kerious.engine.input;

public interface InputManager {
	
	void initialize();

	void setTouchResponder(TouchResponder touchResponder);
	TouchResponder getTouchResponder();

	KeyboardResponder getKeyboardResponder();
	void setKeyboardResponder(KeyboardResponder keyboardResponder);
	
	
}
