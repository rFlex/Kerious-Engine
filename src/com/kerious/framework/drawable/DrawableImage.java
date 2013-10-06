/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.drawable
// DrawableImage.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 8 sept. 2012 at 16:47:18
////////

package com.kerious.framework.drawable;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kerious.framework.Application;

public class DrawableImage extends TextureRegionDrawable {

	////////////////////////
	// VARIABLES
	////////////////

	protected Application application;
	protected String currentSprite;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public DrawableImage(Application application, String spriteName) {
		super(application.library.retrieveSprite(spriteName).toTextureRegion());
		
		this.application = application;
		this.currentSprite = spriteName;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// SETTERS
	////////////////
	
	public void setSprite(String spriteName) {
		this.application.library.retrieveSprite(spriteName).fillTextureRegion(this.getRegion());
		this.currentSprite = spriteName;
	}

	////////////////////////
	// GETTERS
	////////////////
	
	public String getCurrentSprite() {
		return this.currentSprite;
	}

}
