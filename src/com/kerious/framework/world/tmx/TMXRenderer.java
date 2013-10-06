/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// TMXRenderer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 6, 2012 at 9:22:41 PM
////////

package com.kerious.framework.world.tmx;

import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.world.TrackingCamera;

public class TMXRenderer implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////

	private TMXCache _background;
	private TMXCache _foreground;
	private SpriteCache _spriteCache;
	private TrackingCamera _camera;
	private int _tileWidth;
	private int _tileHeight;
	private int _cacheTileWidth;
	private int _cacheTileHeight;
	private int[][] _visibleArea; 
	private boolean _ownsBatch;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TMXRenderer(SpriteCache cache, TMXAtlas tmxAtlas, TiledMap map, int cachedTileWidth, int cachedTileHeight) {
		this._spriteCache = cache;
		this._ownsBatch = false;
		this.init(map, tmxAtlas, cachedTileWidth, cachedTileHeight);
	}
	
	public TMXRenderer(TiledMap map, TMXAtlas tmxAtlas, int cachedTileWidth, int cachedTileHeight) {
		this._spriteCache = this.createCacheForMap(map);
		this._ownsBatch = true;
		this.init(map, tmxAtlas, cachedTileWidth, cachedTileHeight);
	}
	
	////////////////////////
	// METHODS
	////////////////

	private void init(TiledMap map, TMXAtlas tmxAtlas, int cachedTileWidth, int cachedTileHeight) {
		this._background = new TMXCache(map, tmxAtlas, this._spriteCache, false, cachedTileWidth, cachedTileHeight);
		this._foreground = new TMXCache(map, tmxAtlas, this._spriteCache, true, cachedTileWidth, cachedTileHeight);
		
		this._cacheTileWidth = cachedTileHeight;
		this._cacheTileHeight = cachedTileHeight;
		this._tileWidth = map.tileWidth;
		this._tileHeight = map.tileHeight;
		
		this._visibleArea = new int[2][2];
	}
	
	private SpriteCache createCacheForMap(TiledMap map) {
		int cacheSize = 0;

		for (TiledLayer layer : map.layers) {
			for (int y = 0; y < layer.tiles.length; y++) {
				for (int x = 0; x < layer.tiles[y].length; x++) {
					if (layer.tiles[y][x] != 0) {
						cacheSize++;
					}
				}
			}
		}
		return new SpriteCache(cacheSize, false);
	}
	
	private void computeVisibleArea() {
		final float cameraPositionX = this._camera.position.x - this._camera.viewportWidth / 2;
		final float cameraPositionY = this._camera.position.y - this._camera.viewportHeight / 2;
		
		final int startCacheX = (int)(cameraPositionX / this._tileWidth) / this._cacheTileWidth;
		final int startCacheY = (int)(cameraPositionY / this._tileHeight) / this._cacheTileHeight;
		final int endCacheX = (int)((cameraPositionX + this._camera.viewportWidth) / this._tileWidth) / this._cacheTileWidth;
		final int endCacheY = (int)((cameraPositionY + this._camera.viewportHeight) / this._tileHeight) / this._cacheTileHeight;
		
		this._visibleArea[0][0] = startCacheX;
		this._visibleArea[0][1] = startCacheY;
		this._visibleArea[1][0] = endCacheX;
		this._visibleArea[1][1] = endCacheY;
	}
	
	public void renderBackground() {
		if (this._camera != null) {
			this._spriteCache.setProjectionMatrix(this._camera.combined);
			this.computeVisibleArea();
		}
		
		if (this._background != null) {
			this._background.render(this._visibleArea[0][0], this._visibleArea[0][1], this._visibleArea[1][0], this._visibleArea[1][1]);
		}
	}
	
	public void renderForeground() {
		if (this._foreground != null) {
			this._foreground.render(this._visibleArea[0][0], this._visibleArea[0][1], this._visibleArea[1][0], this._visibleArea[1][1]);
		}
	}
	
	@Override
	public void dispose() {
		if (this._ownsBatch && this._spriteCache != null) {
			this._spriteCache.dispose();
			this._spriteCache = null;
			this._background = null;
			this._foreground = null;
		}
	}

	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public void setCamera(TrackingCamera camera) {
		this._camera = camera;
	}
}
