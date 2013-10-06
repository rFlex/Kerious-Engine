/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// TMXTileSet.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 7, 2012 at 1:05:24 AM
////////

package com.kerious.framework.world.tmx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileSet;

public class TMXTileSet {

	////////////////////////
	// VARIABLES
	////////////////
	
	public Texture texture;
	public TextureRegion[][] regions;
	public int firstGID;
	public int lastGID;
	public int tilePerLine;
	public int tileWidth;
	public int tileHeight;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TMXTileSet(String mapPath, TileSet tileSet) {
		this.texture = new Texture(tileSet.imageName.substring("../".length()));
		this.firstGID = tileSet.firstgid;
		this.lastGID = tileSet.firstgid +
									((this.texture.getWidth() * this.texture.getHeight()) /
										(tileSet.tileWidth * tileSet.tileHeight)) - 1;
		this.tilePerLine = this.texture.getWidth() / tileSet.tileWidth;
		this.tileWidth = tileSet.tileWidth;
		this.tileHeight = tileSet.tileHeight;
		this.regions = TextureRegion.split(this.texture, this.tileWidth, this.tileHeight);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public boolean contains(int id) {
		return id >= this.firstGID && id < this.lastGID;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public TextureRegion getRegionForID(int id) {
		id -= this.firstGID;
		int x = 0;
		int y = 0;
		int tileLineLen = this.tilePerLine;
		
		while (id >= tileLineLen) {
			id -= tileLineLen;
			y++;
		}
		
		x = id;
		return this.regions[y][x];
	}
	
}
