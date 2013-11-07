/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// Renderer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 4:57:15 PM
////////

package net.kerious.engine.renderer;

import java.io.IOException;

import net.kerious.engine.view.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class LibgdxRenderer implements Renderer {

	////////////////////////
	// VARIABLES
	////////////////
	
	private LibgdxDrawingContext drawingContext;
	private boolean disposed;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public LibgdxRenderer() {
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void initialize() {
		this.drawingContext = new LibgdxDrawingContext();
	}
	
	public void render(View view) {
		Gdx.gl.glClearColor(0.45f, 0.33f, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		this.drawingContext.startDrawing();
		view.draw(this.drawingContext, 1);
		this.drawingContext.endDrawing();
	}

	@Override
	public void dispose() {
		if (this.drawingContext != null) {
			try {
				this.drawingContext.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.drawingContext = null;
		}
		this.disposed = true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public boolean isDisposed() {
		return this.disposed;
	}

	@Override
	public float getWindowWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getWindowHeight() {
		return Gdx.graphics.getHeight();
	}

}
