/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.library
// AtlasAttributes.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 15 ao�t 2012 at 22:11:36
////////

package com.kerious.framework.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.exceptions.KeriousException;

public class AtlasAttributes implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Texture texture;
	final private String path;
	final private Library library;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public AtlasAttributes(Library library, String path) {
		this.path = path;
		this.library = library;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public final void load() {
		texture = new Texture(Gdx.files.internal(path));
	}
	
	public void dispose() {
		if (texture != null) {
			texture.dispose();
			texture = null;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	/**
	 * Return the loaded Texture, if any. Otherwise, this method will raise a KSException.
	 * @return the loaded texture.
	 */
	public final Texture getTexture() {
		
		if (texture == null) {
			throw new KeriousException("Trying to use a texture which was not loaded before. (Texture path:" + path + ")");
		}
		
		return texture;
	}
	
	public final String getTexturePath() {
		return path;
	}
	
	public final Library getLibrary() {
		return library;
	}

}
