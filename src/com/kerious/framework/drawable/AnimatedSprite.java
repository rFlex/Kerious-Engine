/////////////////////////////////////////////////
	// Project : killing-sight
// Package : com.kerious.killingsight.drawable
// AnimatedSprite.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 9, 2012 at 6:33:05 PM
////////

package com.kerious.framework.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kerious.framework.library.SpriteAttributes;

public class AnimatedSprite {

	////////////////////////
	// VARIABLES
	////////////////

	private SpriteAttributes attributes;
	private float animationSpeed;
	private float stateTime;
	private int playMode;
	private boolean animated;
	private TextureRegion region;
	private Color color;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AnimatedSprite(SpriteAttributes spriteAttributes) {
		this();
		this.setAttributes(spriteAttributes);
	}
	
	public AnimatedSprite() {
		this.color = new Color(Color.WHITE);
	}

	////////////////////////
	// METHODS
	////////////////

	public void act(float delta) {
		if (this.animated) {
			this.stateTime += delta * animationSpeed;
		}
		if (this.attributes != null) {
			this.attributes.getAnimation().setPlayMode(this.playMode);
			this.region = this.attributes.getAnimation().getKeyFrame(this.stateTime);
		} else {
			this.region = null;
		}

	}
	
	public void draw(SpriteBatch spriteBatch, float alphaModulation, float x, float y, float width, float height, float rotation, Color color) {
		if (this.region != null) {
			final Color oldColor = spriteBatch.getColor();
			this.color.set(color);
			this.color.a *= alphaModulation;
				
			spriteBatch.setColor(this.color);
				
			spriteBatch.draw(region, x, y, width / 2, height / 2, width, height, 1, 1, rotation);
				
			spriteBatch.setColor(oldColor);
		}
	}
	
	public final void animate(boolean animated) {
		this.animated = animated;
	}
	
	public final void reset() {
		this.stateTime = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final void setTile(int tileNumber) {
		this.stateTime = this.attributes.getAnimation().frameDuration * (float)tileNumber;
	}
	
	public final SpriteAttributes getAttributes() {
		return this.attributes;
	}
	
	public final void setAttributes(SpriteAttributes attributes) {
		this.attributes = attributes;
	}
	
	public final void setAnimationSpeed(float animationSpeed) {
		if (animationSpeed < 0) {
			animationSpeed = 0;
		}
		
		this.animationSpeed = animationSpeed;
	}
	
	public final float getAnimationSpeed() {
		return this.animationSpeed;
	}
	
	public final void setPlayMode(int playMode) {
		this.playMode = playMode;
	}
	
	public final int getPlayMode() {
		return this.playMode;
	}

	public boolean isAnimationFinished() {
		return this.attributes != null ? this.attributes.getAnimation().isAnimationFinished(this.stateTime) : true;
	}
	
}
