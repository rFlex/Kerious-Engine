/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world
// GameMap.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 29 sept. 2012 at 17:22:08
////////

package com.kerious.framework.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Group;

public class GameMap {

	////////////////////////
	// VARIABLES
	////////////////
	
	private TiledMap map;
	private TileAtlas tileAtlas;
	private TileMapRenderer tileMapRenderer;
	private GameWorld gameWorld;
	private Group collisionBlocks;
	private OrthographicCamera camera;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public GameMap(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		this.camera = gameWorld.getTrackingCamera();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void renderFloor() {
		if (tileMapRenderer != null) {
			tileMapRenderer.render(this.camera);
		}
	}
	
	public void renderAerial() {
		
	}
	
	public void unload() {
		this.map = null;
		
		if (this.tileAtlas != null) {
			this.tileAtlas.dispose();
			this.tileAtlas = null;
		}
		
		if (this.tileMapRenderer != null) {
			this.tileMapRenderer.dispose();
			this.tileMapRenderer = null;
		}

		if (this.collisionBlocks != null) {
			this.collisionBlocks.remove();
			this.collisionBlocks = null;
		}
	}
	
	public void load(String mapPath, String mapName) {
		this.unload();
		
		this.map = TiledLoader.createMap(mapPath + mapName);
		
		if (this.gameWorld.application.drawingEnabled) {
			this.tileAtlas = new TileAtlas(map, Gdx.files.internal(mapPath));
			this.tileMapRenderer = new TileMapRenderer(map, tileAtlas, 16, 16);
		}
		
		this.collisionBlocks = this.createCollisionBlock(this.map);
		this.gameWorld.addActor(this.collisionBlocks);
	}
	
	private Group createCollisionBlock(TiledMap map) {
		Group group = new Group();
		
		group.addActor(new CollisionBlock(0, 0, 800, 600));
		
		return group;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getMapWidth() {
		return 0;
	}
	
	public int getMapHeight() {
		return 0;
	}
	
}
