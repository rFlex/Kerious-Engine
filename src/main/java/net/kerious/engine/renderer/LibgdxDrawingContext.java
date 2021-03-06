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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class LibgdxDrawingContext implements DrawingContext {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private SpriteBatch spriteBatch;
	final private ShapeRenderer shapeRenderer;
	final private Matrix4 projectionMatrix;
	
	private Projection projection;
	private boolean spriteBatchDrawing;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public LibgdxDrawingContext() {
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.projectionMatrix = new Matrix4();

		this.updateProjectionFromWindowSize();
	}

	////////////////////////
	// METHODS
	////////////////

	public void updateProjectionFromWindowSize() {
		this.setProjection(new Projection((float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight()));
	}
	
	final public void beginBatch() {
		if (!this.spriteBatchDrawing) {
			this.spriteBatchDrawing = true;
			this.spriteBatch.begin();
		}
	}

	final public void endBatch() {
		if (this.spriteBatchDrawing) {
			this.spriteBatchDrawing = false;
			this.spriteBatch.end();
		}
	}
	
	@Override
	public void fillRectangle(Color color, float x, float y, float width, float height, float rotation) {
		this.endBatch();
		
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.setColor(color);
		this.shapeRenderer.rect(x, y, width, height, width / 2, height / 2, rotation);
		this.shapeRenderer.end();
		
		this.beginBatch();
	}
	
	@Override
	public void close() throws IOException {
		this.spriteBatch.dispose();
	}
	
	@Override
	public void limitRenderingBounds(Rectangle renderingBounds) {
		ScissorStack.pushScissors(renderingBounds);
	}

	@Override
	public void unlimitRenderingBounds() {
		ScissorStack.popScissors();
	}
	
	@Override
	public void fillRectangle(TextureRegion textureRegion, float x, float y, float width, float height, float alpha, float rotation) {
		this.spriteBatch.draw(textureRegion, x, y, 0, 0, width, height, 1, 1, rotation);
	}
	
	public final void updateProjection() {
		this.spriteBatch.setProjectionMatrix(this.projectionMatrix);
		this.shapeRenderer.setProjectionMatrix(this.projectionMatrix);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public float getTint() {
		return this.spriteBatch.getColor().toFloatBits();
	}

	@Override
	public void setTint(float tint) {
		this.spriteBatch.setColor(tint);
	}

	@Override
	public Projection getProjection() {
		return this.projection;
	}

	@Override
	public void setProjection(Projection projection) {
		if (projection == null) {
			throw new IllegalArgumentException("projection may not be null");
		}
		
		this.projection = projection;
		this.projectionMatrix.setToOrtho(0, projection.width, 0, projection.height, 0, 100);
		
		this.updateProjection();
	}

	@Override
	public boolean isVisibleInContext(Rectangle r) {
//		return true;
		return 0 < r.x + r.width && this.projection.width > r.x && 0 < r.y + r.height && this.projection.height > r.y;
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		return this.projectionMatrix;
	}

	@Override
	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		this.projectionMatrix.set(projectionMatrix);
		this.updateProjection();
	}

	@Override
	public Matrix4 getTransformMatrix() {
		return this.spriteBatch.getTransformMatrix();
	}

	@Override
	public void setTransformMatrix(Matrix4 transformMatrix) {
		this.spriteBatch.setTransformMatrix(transformMatrix);
		this.shapeRenderer.setTransformMatrix(transformMatrix);
	}

	@Override
	public SpriteBatch getBatch() {
		return this.spriteBatch;
	}
}
