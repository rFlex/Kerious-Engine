/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.map
// Map.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 27, 2013 at 3:13:12 AM
////////

package net.kerious.engine.map;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricStaggeredTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

public class GameMap {

	////////////////////////
	// VARIABLES
	////////////////
	
	private TiledMapRenderer renderer;
	private TiledMap tiledMap;
	private Array<MapLayerView> layersView;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public GameMap() {
		this.layersView = new Array<MapLayerView>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	final private void ensureTiledMap() {
		if (this.tiledMap == null) {
			throw new KeriousException("The map has no tiledmap set");
		}
	}
	
	protected void createLayersView(TiledMapRenderer tiledMapRenderer) {
		this.ensureTiledMap();
		
		MapLayers mapLayers = tiledMap.getLayers();
		
		for (int i = 0; i < mapLayers.getCount(); i++) {
			MapLayer layer = mapLayers.get(i);
			if (layer instanceof TiledMapTileLayer) {
				this.layersView.add(new MapLayerView(tiledMapRenderer, (TiledMapTileLayer)layer));
			}
		}
	}
	
	public void createOrthogonalMapLayersViews(KeriousEngine engine) {
		this.ensureTiledMap();
		
		this.renderer = new OrthogonalTiledMapRenderer(this.tiledMap, engine.getRenderer().getContext().getBatch());
		this.createLayersView(this.renderer);
	}
	
	public void createIsometricMapLayersViews(KeriousEngine engine) {
		this.ensureTiledMap();
		
		this.renderer = new IsometricTiledMapRenderer(this.tiledMap, engine.getRenderer().getContext().getBatch());
		this.createLayersView(this.renderer);
	}
	
	public void createIsometricStaggeredMapLayersViews(KeriousEngine engine) {
		this.ensureTiledMap();
		
		this.renderer = new IsometricStaggeredTiledMapRenderer(this.tiledMap, engine.getRenderer().getContext().getBatch());
		this.createLayersView(this.renderer);
	}
	
	public void createHexagonalStaggeredMapLayersViews(KeriousEngine engine) {
		this.ensureTiledMap();
		
		this.renderer = new HexagonalTiledMapRenderer(this.tiledMap, engine.getRenderer().getContext().getBatch());
		this.createLayersView(this.renderer);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Array<MapLayerView> getLayersView() {
		return layersView;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}
}
