/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.drawable
// DrawableUnit.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 15 ao�t 2012 at 22:35:37
////////

package com.kerious.framework.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kerious.framework.Application;
import com.kerious.framework.library.SpriteAttributes;

public class AnimatedSpriteActor extends DrawableActor {

	////////////////////////
	// VARIABLES
	////////////////
	
	final protected Application application;
	final protected AnimatedSprite sprite;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public AnimatedSpriteActor(Application application) {
		this(application, (String)null, true);
	}
	
	public AnimatedSpriteActor(Application application, boolean enableGfx) {
		this(application, (String)null, enableGfx);
	}
	
	public AnimatedSpriteActor(Application application, String spriteName, boolean enableGfx) {
		this(application, spriteName != null ? application.library.retrieveSprite(spriteName) : null, enableGfx);
	}
	
	public AnimatedSpriteActor(Application application, SpriteAttributes attributes, boolean enableGfx) {
		this.application = application;
		
		if (enableGfx) {
			this.sprite = new AnimatedSprite(attributes);
		} else {
			this.sprite = null;
		}
		
		if (attributes != null) {
			setSize(attributes.getWidth(), attributes.getHeight());
		}
		this.setTransform(false);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (this.sprite != null) {
			this.sprite.act(delta);
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		this.sprite.draw(batch, parentAlpha, getX(), getY(), getWidth(), getHeight(), getRotation(), getColor());
		
		super.draw(batch, parentAlpha);
	}
	
	public void scaleToSpriteAttributes() {
		this.scaleToSpriteAttributes(this.getSprite() != null ? this.getSprite().getAttributes() : null);
	}
	
	public void scaleToSpriteAttributes(SpriteAttributes spriteAttributes) {
		if (spriteAttributes != null) {
			this.setDimension(spriteAttributes.getWidth(), spriteAttributes.getHeight());
		} else {
			this.setDimension(0, 0);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final AnimatedSprite getSprite() {
		return this.sprite;
	}
	
	public final void setSpriteAttributesFromIdentifier(String identifier) {
		this.sprite.setAttributes(this.application.library.retrieveSprite(identifier));
	}

	public final void setPlayMode(int playMode) {
		this.sprite.setPlayMode(playMode);
	}
	
}
