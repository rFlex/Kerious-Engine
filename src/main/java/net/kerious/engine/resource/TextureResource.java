/////////////////////////////////////////////////
// Project : Kerious-Engine
// Package : net.kerious.engine.resource
// TextureResource.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 15, 2014 at 3:18:35 PM
////////

package net.kerious.engine.resource;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class TextureResource extends Resource<Texture> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TextureResource(FileHandle fileHandle) {
		super(Texture.class, fileHandle);
	}
	
	public TextureResource(String fileName) {
		super(Texture.class, fileName);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
