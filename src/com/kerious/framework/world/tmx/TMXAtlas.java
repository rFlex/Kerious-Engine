/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// TMXAtlas.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 6, 2012 at 9:24:14 PM
////////

package com.kerious.framework.world.tmx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileSet;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

public class TMXAtlas {

	////////////////////////
	// VARIABLES
	////////////////

	private ArrayList<TMXTileSet> _tilesets;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TMXAtlas(String mapPath, TiledMap map) {
		
		this._tilesets = new ArrayList<TMXTileSet>();
		int layerSize = map.tileSets.size();
		
		for (int layer = 0; layer < layerSize; layer++) {
			TileSet tileSet = map.tileSets.get(layer);
			TMXTileSet TMXTileSet = new TMXTileSet(mapPath, tileSet);

			this._tilesets.add(TMXTileSet);
		}
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void dispose() {
		for (TMXTileSet tileSet : this._tilesets) {
			if (tileSet.texture != null) {
				tileSet.texture.dispose();
				tileSet.texture = null;
			}
		}
	}
	
	public void clean() {
		for (TMXTileSet tileSet : this._tilesets) {
			tileSet.regions = null;
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public TMXTileSet getTileSetForID(int id) {
		for (TMXTileSet tileSet : this._tilesets) {
			if (tileSet.contains(id)) {
				return tileSet;
			}
		}
		return null;
	}

	public Texture getTextureForID(int id) {
		Texture texture = null;
		TMXTileSet tileSet = this.getTileSetForID(id);
		
		if (tileSet != null) {
			texture = tileSet.texture;
		}
		
		return texture;
	}
	
	public TextureRegion getTextureRegionForID(int id) {
		TMXTileSet tileSet = this.getTileSetForID(id);
		TextureRegion textureRegion = null;

		if (tileSet != null) {
			textureRegion = tileSet.getRegionForID(id);
		}
		
		return textureRegion;
	}
	
	public ArrayList<TMXTileSet> getTileSets() {
		return this._tilesets;
	}
}
