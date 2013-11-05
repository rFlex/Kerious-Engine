/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// DrawingContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 5, 2013 at 1:35:08 PM
////////

package net.kerious.engine.renderer;

import java.io.Closeable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface DrawingContext extends Closeable {

	void fillRectangle(Color color, float x, float y, float width, float height);
	
	void startDrawing();
	void endDrawing();
	SpriteBatch getBatch();
	
}
