/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// NullContext.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 27, 2013 at 3:24:33 AM
////////

package net.kerious.engine.renderer;

import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class NullDrawingContext implements DrawingContext {

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
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillRectangle(Color color, float x, float y, float width,
			float height, float rotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillRectangle(TextureRegion textureRegion, float x, float y,
			float width, float height, float alpha, float rotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void limitRenderingBounds(Rectangle renderingBounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlimitRenderingBounds() {
		// TODO Auto-generated method stub
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public boolean isVisibleInContext(Rectangle renderingBounds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getTint() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTint(float tint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix4 getTransformMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTransformMatrix(Matrix4 transformMatrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Projection getProjection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProjection(Projection projection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SpriteBatch getBatch() {
		// TODO Auto-generated method stub
		return null;
	}
}
