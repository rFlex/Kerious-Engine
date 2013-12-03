/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.gamecontroller
// KeyEventListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 3, 2013 at 4:10:26 PM
////////

package net.kerious.engine.gamecontroller;

public interface KeyListener {

	void onKeyPressed(GameController gameController, int key);
	void onKeyReleased(GameController gameController, int key);
	
}
