/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// RelativeFileResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 28, 2013 at 2:03:06 PM
////////

package net.kerious.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class RelativeFileResolver implements FileHandleResolver {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	@Override
	public FileHandle resolve(String fileName) {
		if (Gdx.files != null) {
			return Gdx.files.internal(fileName);
		}
		
		return new FileHandle(fileName);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
