/////////////////////////////////////////////////
// Project : Kerious-Engine
// Package : net.kerious.engine.utils
// ClasspathFileHandleResolver.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 16, 2014 at 8:12:17 PM
////////

package net.kerious.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ClasspathFileHandleResolver implements FileHandleResolver { 

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ClasspathFileHandleResolver() {

	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public FileHandle resolve(String fileName) {
		return Gdx.files.classpath(fileName);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
