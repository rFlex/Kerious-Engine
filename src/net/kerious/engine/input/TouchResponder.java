/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.input
// TouchResponder.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 10, 2013 at 3:14:51 AM
////////

package net.kerious.engine.input;

import com.badlogic.gdx.math.Vector2;

public interface TouchResponder {

	TouchResponder getBestTouchResponderForLocation(float x, float y);
	Vector2 convertScreenToTouchResponderLocation(float x, float y, Vector2 output);
	
	void onTouchDown(int pointer, float x, float y, int button);
	void onTouchUp(int pointer, float x, float y, int button);
	void onTouchDragged(int pointer, float x, float y, int button);
	void onTouchOver(float x, float y);
	
	boolean isAvailableForTouchResponding(); 
	
}
