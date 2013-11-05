/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.drawable
// ColorDrawable.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 10:50:50 AM
////////

package net.kerious.engine.drawable;

import net.kerious.engine.renderer.DrawingContext;

import com.badlogic.gdx.graphics.Color;

public class DrawableColor implements Drawable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Color color;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public DrawableColor(Color color) {
		this();
		
		this.setColor(color);
	}
	
	public DrawableColor() {
		this.color = new Color(Color.WHITE);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void draw(DrawingContext context, float x, float y, float width, float height) {
		context.fillRectangle(this.color, x, y, width, height);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (color != null) {
			this.color.set(color);
		} else {
			this.color.set(0, 0, 0, 0);
		}
	}
}
