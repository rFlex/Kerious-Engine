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

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.renderer.DrawingContext;
import net.kerious.engine.view.View;

public class MapLayerView extends View {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private TiledMapRenderer tiledMapRenderer;
	final private TiledMapTileLayer layer;

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
		
		this.tiledMapRenderer = tiledMapRenderer;
		this.layer = layer;
		this.setSize(layer.getWidth() * layer.getTileWidth(), layer.getHeight() * layer.getTileHeight());
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void drawView(DrawingContext context, float width, float height, float alpha) {
		this.tiledMapRenderer.setView(context.getProjectionMatrix(), this.getX(), this.getY(), width, height);
		this.tiledMapRenderer.renderTileLayer(this.layer);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
