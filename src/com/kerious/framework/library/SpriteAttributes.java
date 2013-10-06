/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.library
// SpriteAttributes.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 15 ao�t 2012 at 22:09:57
////////

package com.kerious.framework.library;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SpriteAttributes {
	
	////////////////////////
	// VARIABLES
	////////////////

	final public AtlasAttributes atlasAttributes;
	final private String spriteName;
	
	private int textureX;
	private int textureY;
	private int textureWidth;
	private int textureHeight;
	private int tileLen;
	private Animation animation;
	private TextureRegion[] regions;

	private int tileHeight;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public SpriteAttributes(String spriteName, AtlasAttributes atlasAttributes, int textureX, int textureY, int textureWidth, int textureHeight) {
		this(spriteName, atlasAttributes, textureX, textureY, textureWidth, textureHeight, 1, 1);
	}
	
	public SpriteAttributes(String spriteName, AtlasAttributes atlasAttributes, int textureX, int textureY, int textureWidth, int textureHeight, int tileLen, int tileHeight) {
		this.spriteName = spriteName;
		this.atlasAttributes = atlasAttributes;
		
		this.textureX = textureX;
		this.textureY = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.tileLen = tileLen;
		this.tileHeight = tileHeight;
		
//		this.animation = new Animation(0.01f, this.getRegions());
	}
	
	////////////////////////
	// METHODS
	////////////////

	public TextureRegion toTextureRegion() {
		TextureRegion region = new TextureRegion();
		
		this.fillTextureRegion(region);
		
		return region;
	}
	
	public Sprite toSprite() {
		return new Sprite(this.toTextureRegion());
	}
	
	public TextureRegionDrawable toDrawable() {
		return new TextureRegionDrawable(this.toTextureRegion());
	}
	
	public void fillTextureRegion(TextureRegion region) {
		region.setTexture(this.atlasAttributes.getTexture());
		region.setRegion(this.textureX, this.textureY, this.textureWidth, this.textureHeight);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final int getTextureX() {
		return textureX;
	}
	
	public final int getTextureY() {
		return textureY;
	}
	
	public final int getWidth() {
		return textureWidth;
	}
	
	public final int getHeight() {
		return textureHeight;
	}
	
	public final int getTileLen() {
		return this.tileLen;
	}

	public final TextureRegion[] getRegions() {
		if (this.regions == null) {
			TextureRegion[][] textureRegions = null;
			
			if (this.tileLen > 1) {
				textureRegions = TextureRegion.split(this.atlasAttributes.getTexture(), this.textureWidth, this.textureHeight);
			} else {
				textureRegions = new TextureRegion[1][1];
				textureRegions[0][0] = this.toTextureRegion();
			}
			
			this.regions = new TextureRegion[this.tileLen * this.tileHeight];
			for (int j = 0; j < this.tileHeight; j++) {
				for (int i = 0; i < this.tileLen; i++) {
					this.regions[j * this.tileLen + i] = textureRegions[j][i];
					this.regions[j * this.tileLen + i].setRegion(this.textureX + (i * this.textureWidth), this.textureY + (j * this.textureHeight), this.textureWidth, this.textureHeight);
				}
			}
		}
		return this.regions;
	}
	
	public final Animation getAnimation() {
		if (this.animation == null) {
			this.animation = new Animation(0.01f, this.getRegions());
		}
		
		return this.animation;
	}
	
	public final String getSpriteName() {
		return this.spriteName;
	}
	
}
