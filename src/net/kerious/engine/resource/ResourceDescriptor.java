/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceDescriptor.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 27, 2013 at 3:22:08 PM
////////

package net.kerious.engine.resource;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ResourceDescriptor {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Class<?> resourceType;
	private String fileName;
	private FileHandle fileHandle;
	private boolean loaded;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ResourceDescriptor() {
		
	}
	
	public ResourceDescriptor(Class<?> resourceType, String fileName) {
		this.resourceType = resourceType;
		this.fileName = fileName;
	}
	
	public ResourceDescriptor(Class<?> resourceType, FileHandle fileHandle) {
		this.resourceType = resourceType;
		this.fileName = fileHandle.path();
		this.fileHandle = fileHandle;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void resolveFilePath(FileHandleResolver resolver) {
		this.fileHandle = resolver.resolve(this.fileName);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Class<?> getResourceType() {
		return resourceType;
	}

	public void setResourceType(Class<?> resourceType) {
		this.resourceType = resourceType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FileHandle getFileHandle() {
		return fileHandle;
	}

	public void setFileHandle(FileHandle fileHandle) {
		this.fileHandle = fileHandle;
	}
	
	public boolean isFilePathResolved() {
		return this.fileHandle != null;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
}
