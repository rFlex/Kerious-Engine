/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// TMXMap.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 6, 2012 at 9:12:09 PM
////////

package com.kerious.framework.world.tmx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.collisions.CollisionHandler;
import com.kerious.framework.collisions.GraphNode;
import com.kerious.framework.collisions.NavigationMesh;
import com.kerious.framework.exceptions.TMXMapException;
import com.kerious.framework.utils.FileManager;
import com.kerious.framework.utils.StopWatch;
import com.kerious.framework.world.TrackingCamera;
import com.kerious.framework.world.entities.Entity;

public class TMXMap extends Group implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////

	private String _mapName;
	private String _mapPath;
	private TMXAtlas _tmxAtlas;
	private TMXRenderer _tmxRenderer;
	private CollisionHandler _collisionHandler;
	private NavigationMesh	_navigationMesh;
	private int _mapCacheWidth;
	private int _mapCacheHeight;
	private boolean _enableRenderer;
	private boolean _navigationMeshDrawingEnabled;
	private ShapeRenderer debugRenderer;
	private TrackingCamera _camera;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public TMXMap(String mapPath, int mapCacheWidth, int mapCacheHeight, boolean enableRenderer) {
		if (mapPath.endsWith("/")) {
			this._mapPath = mapPath;
		} else {
			this._mapPath = mapPath + "/";
		}
		
		this._mapCacheWidth = mapCacheWidth;
		this._mapCacheHeight = mapCacheHeight;
		this._enableRenderer = enableRenderer;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void dispose() {
		if (this._tmxAtlas != null) {
			this._tmxAtlas.dispose();
			this._tmxAtlas = null;
		}
		if (this._tmxRenderer != null) {
			this._tmxRenderer.dispose();
			this._tmxRenderer = null;
		}
	}
	
	public void loadMap(FileManager fileManager, String mapName) {
		FileHandle mapHandle = fileManager.getAssetFileHandle(this._mapPath +  mapName);
		
		if (!mapHandle.exists()) {
			throw new TMXMapException(mapName, "Couldn't open map file at " + mapHandle.path());
		}
		
		StopWatch sw = new StopWatch();
		sw.start();
		
		TiledMap tiledMap = TiledLoader.createMap(mapHandle);
		this.setWidth(tiledMap.width * tiledMap.tileWidth);
		this.setHeight(tiledMap.height * tiledMap.tileHeight);
		
		if (this._enableRenderer) {
			this._tmxAtlas = new TMXAtlas(this._mapPath, tiledMap);
			this._tmxRenderer = new TMXRenderer(tiledMap, this._tmxAtlas, this._mapCacheWidth, this._mapCacheHeight);
			this.debugRenderer = new ShapeRenderer();
			this._tmxAtlas.clean();
		}
		
		this._collisionHandler = new CollisionHandler(tiledMap);
		this._navigationMesh = new NavigationMesh(tiledMap, this._collisionHandler);
		this._mapName = mapName;
		
		System.out.println("Loaded " + mapName + " of size " + tiledMap.width + "/" + tiledMap.height + " in " + sw.stringCurrent());
	}
	
	public void drawBackground(SpriteBatch batch) {
		if (this._tmxRenderer != null) {
			this._tmxRenderer.renderBackground();
		}
	}
	
	public void drawForeground(SpriteBatch batch) {
		if (this._tmxRenderer != null) {
			this._tmxRenderer.renderForeground();
		}
		
		if (this._navigationMeshDrawingEnabled) {
			this.drawNavigationMesh();
		}
	}
	
	public void drawNavigationMesh() {
		debugRenderer.setProjectionMatrix(this._camera.combined);
//		for (GraphNode node : this._navigationMesh.getGraphNodes()) {
//				debugRenderer.begin(ShapeType.Rectangle);
//				Rectangle rect = node.getRectangle();
//				debugRenderer.setColor(Color.BLUE);
//				debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//				debugRenderer.end();
//				debugRenderer.begin(ShapeType.Line);
//				debugRenderer.setColor(Color.ORANGE);
//				for (GraphNode neighbour : node.getNeighbours()) {
//					Rectangle otherRect = neighbour.getRectangle();
//					debugRenderer.line(rect.getX() + (rect.getWidth() / 2),
//										rect.getY() + (rect.getHeight() / 2),
//										otherRect.getX() + (otherRect.getWidth() / 2),
//										otherRect.getY() + (otherRect.getHeight() / 2));
//				}
//				debugRenderer.end();				
//			}
		this._collisionHandler.drawGrid(debugRenderer);
//		this._collisionHandler.drawCollisionRegions(debugRenderer);
	}

	public void setCamera(TrackingCamera camera) {
		if (this._tmxRenderer != null) {
			this._tmxRenderer.setCamera(camera);
			this._camera = camera;
		}
	}
	
	public void trackActorCollision(Entity entity) {
		if (this._collisionHandler != null) {
			this._collisionHandler.trackEntity(entity);
		}
	}
	
	public void untrackActorCollision(Entity entity) {
		if (this._collisionHandler != null) {
			this._collisionHandler.untrackEntity(entity);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isLoaded() {
		return this._tmxAtlas != null;
	}
	
	public String getMapName() {
		return this._mapName;
	}

	public CollisionHandler getCollisionHandler() {
		return _collisionHandler;
	}
	
	public final void enableNavigationMeshDrawing(boolean value) {
		this._navigationMeshDrawingEnabled = value;
	}
	
	public final boolean isNavigationMeshDrawingEnabled() {
		return this._navigationMeshDrawingEnabled;
	}

	public NavigationMesh getNavigationMesh() {
		return this._navigationMesh;
	}

}
