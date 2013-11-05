/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// DrawingContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 5, 2013 at 1:33:42 PM
////////

package net.kerious.engine.renderer;

import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class LibgdxDrawingContext implements DrawingContext {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private SpriteBatch spriteBatch;
	final private ShapeRenderer shapeRenderer;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public LibgdxDrawingContext() {
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void startDrawing() {
		this.spriteBatch.begin();
	}

	@Override
	public void endDrawing() {
		this.spriteBatch.end();
	}

	@Override
	public void fillRectangle(Color color, float x, float y, float width,
			float height) {
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.setColor(color);
		this.shapeRenderer.rect(x, y, width, height);
		this.shapeRenderer.end();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public SpriteBatch getBatch() {
		return this.spriteBatch;
	}

	@Override
	public void close() throws IOException {
		this.spriteBatch.dispose();
	}


}
