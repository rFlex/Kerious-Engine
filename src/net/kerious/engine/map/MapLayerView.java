/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.tmx
// TMXLayerView.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 27, 2013 at 2:59:53 AM
////////

package net.kerious.engine.map;

import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.renderer.DrawingContext;
import net.kerious.engine.view.View;

public class MapLayerView extends View {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private TiledMapRenderer tiledMapRenderer;
	final private TiledMapTileLayer layer;
	final private Matrix4 savedTransformMatrix;
	final private Matrix4 transformMatrix;
	final private Rectangle viewBounds;
	private float mapWidth;
	private float mapHeight;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public MapLayerView(TiledMapRenderer tiledMapRenderer, TiledMapTileLayer layer) {
		if (tiledMapRenderer == null) {
			throw new NullArgumentException("tiledMapRenderer");
		}
		if (layer == null) {
			throw new NullArgumentException("layer");
		}
		
		this.setTouchEnabled(false);
		
		this.viewBounds = new Rectangle();
		this.savedTransformMatrix = new Matrix4();
		this.transformMatrix = new Matrix4();
		this.tiledMapRenderer = tiledMapRenderer;
		this.layer = layer;
		this.mapWidth = this.layer.getWidth() * this.layer.getTileWidth();
		this.mapHeight = this.layer.getHeight() * this.layer.getTileHeight();
		this.viewBounds.set(0, 0, this.mapWidth, this.mapHeight);
		this.resizeToFit();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void drawView(DrawingContext context, float width, float height, float alpha) {
		this.savedTransformMatrix.set(context.getTransformMatrix());
//		System.out.println(this.savedTransformMatrix);
		this.transformMatrix.set(this.savedTransformMatrix);
		this.transformMatrix.scale(this.getScaleWidth(), this.getScaleHeight(), 1);
		context.setTransformMatrix(this.transformMatrix);
		
		Rectangle viewBounds = this.viewBounds;
		this.tiledMapRenderer.setView(context.getProjectionMatrix(), viewBounds.x, viewBounds.y, viewBounds.width, viewBounds.height);
		this.tiledMapRenderer.renderTileLayer(this.layer);
		
		context.setTransformMatrix(this.savedTransformMatrix);
	}
	
	@Override
	public void resizeToFit() {
		this.setSize(this.mapWidth, this.mapHeight);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public float getScaleWidth() {
		return this.getWidth() / this.mapWidth;
	}
	
	public float getScaleHeight() {
		return this.getHeight() / this.mapHeight;
	}
	
	public Rectangle getViewBounds() {
		return viewBounds;
	}
	
	/**
	 * Set an hint about the current visible bounds of the map.
	 * This will prevent from asking to draw the whole map at once
	 * @param viewBounds
	 */
	public void setViewBounds(Rectangle viewBounds) {
		this.viewBounds.set(viewBounds);
	}

	public float getMapWidth() {
		return mapWidth;
	}

	public float getMapHeight() {
		return mapHeight;
	}
}
