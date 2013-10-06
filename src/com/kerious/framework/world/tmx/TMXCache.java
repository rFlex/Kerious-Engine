/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// TMXCache.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 6, 2012 at 9:56:57 PM
////////

package com.kerious.framework.world.tmx;

import java.nio.BufferOverflowException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

public class TMXCache {

	////////////////////////
	// VARIABLES
	////////////////

	private SpriteCache _spriteCache;
	private int cachedTileWidth;
	private int cachedTileHeight;
	private int[][] _cacheIDs;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TMXCache(TiledMap tiledMap, TMXAtlas tmxAtlas, SpriteCache cache, boolean isForeground, int cachedTileWidth, int cachedTileHeight) {

		ArrayList<TiledLayer> layers = new ArrayList<TiledLayer>();
		
		for (TiledLayer layer : tiledMap.layers) {
			String backgroundProperty = layer.properties.get("background");
			final boolean isABackgroundLayer = backgroundProperty != null ? backgroundProperty.equals("true") : false;
			if (isForeground && !isABackgroundLayer) {
				layers.add(layer);
			} else if (!isForeground && isABackgroundLayer) {
				layers.add(layer);
			}
		}
		
		this._spriteCache = cache;
		this._cacheIDs = new int[tiledMap.height / cachedTileHeight + 1][tiledMap.width / cachedTileWidth + 1];
		
		for (int x = 0; x < tiledMap.width; x += cachedTileWidth) {
			for (int y = 0; y < tiledMap.height; y += cachedTileHeight) {
				this._spriteCache.beginCache();
				final int endX = x + cachedTileWidth;
				final int endY = y + cachedTileHeight;
				
				for (TiledLayer layer : layers) {
					this.addLayerToCache(layer, tiledMap, tmxAtlas, x, y, endX, endY);
				}
				
				final int cacheCaseX = x / cachedTileWidth;
				final int cacheCaseY = (tiledMap.height / cachedTileHeight) - (y / cachedTileHeight) - 1;
				
				this._cacheIDs[cacheCaseY][cacheCaseX] = this._spriteCache.endCache();
			}
		}
	}
	
	////////////////////////
	// METHODS
	////////////////

	private void addLayerToCache(TiledLayer layer, TiledMap map, TMXAtlas tmxAtlas, int startX, int startY, int endX, int endY) {
		
		for (int x = startX; x < layer.tiles.length && x < endX; x++) {
			for (int y = startY; y < layer.tiles[x].length && y < endY; y++) {
				final int gid = layer.tiles[y][x];
				final int realX = x * map.tileWidth;
				final int realY = ((map.height - 1) * map.tileHeight) - (y * map.tileHeight);
				
				TextureRegion textureRegion = tmxAtlas.getTextureRegionForID(gid);
				if (textureRegion != null) {
					try {
						this._spriteCache.add(textureRegion, realX, realY);
					} catch (BufferOverflowException e) {
						throw e;
					}
				}
			}
		}
	}
	
	public void render() {
		this.render(0, 0, this._cacheIDs[0].length - 1, this._cacheIDs.length - 1);
	}
	
	public void render(int startCacheX, int startCacheY, int endCacheX, int endCacheY) {
		if (startCacheX < 0) {
			startCacheX = 0;
		}
		
		if (startCacheY < 0) {
			startCacheY = 0;
		}
		
		if (endCacheX >= this._cacheIDs[0].length) {
			endCacheX = this._cacheIDs[0].length - 1;
		}
		
		if (endCacheY >= this._cacheIDs.length) {
			endCacheY = this._cacheIDs.length - 1;
		}
		
		this._spriteCache.begin();
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL10.GL_BLEND);

		for (int y = startCacheY; y <= endCacheY; y++) {
			for (int x = startCacheX; x <= endCacheX; x++) {
				this._spriteCache.draw(this._cacheIDs[y][x]);
			}
		}

		this._spriteCache.end();
	}

	public final int getCachedTileWidth() {
		return cachedTileWidth;
	}

	public final int getCachedTileHeight() {
		return cachedTileHeight;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
