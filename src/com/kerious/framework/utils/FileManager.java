/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.utils
// File.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2012 at 4:37:46 PM
////////

package com.kerious.framework.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;

public class FileManager {

	////////////////////////
	// VARIABLES
	////////////////
	
	private String configPathDesktop;
	private String assetsPathDeskopt;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public FileManager() {
		this.configPathDesktop = "";
		this.assetsPathDeskopt = "";
	}

	////////////////////////
	// METHODS
	////////////////

	public FileHandle getConfigFileHandle(String path) {
		FileHandle handle = null;
		
		if (Gdx.files != null) {
			handle = Gdx.files.getFileHandle("../" + path, FileType.Internal);
		} else {
			handle = new FileHandle(new java.io.File(this.configPathDesktop + path));
		}
		
		return handle;
	}
	
	public FileHandle getAssetFileHandle(String path) {
		FileHandle handle = null;
		
		if (Gdx.files != null) {
			handle = Gdx.files.getFileHandle(path, FileType.Internal);
		} else {
			handle = new FileHandle(new java.io.File(this.assetsPathDeskopt + path));
		}
		
		return handle;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final String getConfigPathDesktop() {
		return configPathDesktop;
	}

	public final void setConfigPathDesktop(String configPathDesktop) {
		this.configPathDesktop = configPathDesktop;
	}

	public final String getAssetsPathDeskopt() {
		return assetsPathDeskopt;
	}

	public final void setAssetsPathDeskopt(String assetsPathDeskopt) {
		this.assetsPathDeskopt = assetsPathDeskopt;
	}
}
