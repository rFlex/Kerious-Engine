/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.drawable
// Drawable.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 10:08:27 AM
////////

package net.kerious.engine.drawable;

import net.kerious.engine.renderer.DrawingContext;

public interface Drawable {
	
	void draw(DrawingContext context, float x, float y, float width, float height, float alpha);

}
