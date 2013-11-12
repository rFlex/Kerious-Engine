/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.input
// KeyboardResponder.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2013 at 12:52:04 AM
////////

package net.kerious.engine.input;

public interface KeyboardResponder {

	void onBecameResponder();
	
	void onResignedResponder();
	
	void onKeyDown(int keycode);
	
	void onKeyUp(int keycode);
	
	void onCharTyped(char c);
	
}
