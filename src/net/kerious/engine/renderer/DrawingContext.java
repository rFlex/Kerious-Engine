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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public interface DrawingContext extends Closeable {

	void fillRectangle(Color color, float x, float y, float width, float height, float rotation);
	void fillRectangle(TextureRegion textureRegion, float x, float y, float width, float height,
			float alpha, float rotation);
	
	void limitRenderingBounds(Rectangle renderingBounds);
	void unlimitRenderingBounds();
	
	boolean isVisibleInContext(Rectangle renderingBounds);
	
	float getTint();
	void setTint(float tint);
	
	Matrix4 getProjectionMatrix();
	void setProjectionMatrix(Matrix4 projectionMatrix);
	
	Matrix4 getTransformMatrix();
	void setTransformMatrix(Matrix4 transformMatrix);
	
	Projection getProjection();
	void setProjection(Projection projection);
	
	SpriteBatch getBatch();
	
}
