/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceBundle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:03:48 AM
////////

package net.kerious.engine.resource;

import com.badlogic.gdx.utils.Array;

public class ResourceBundle {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Array<ResourceInfo> resources;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ResourceBundle() {
		this.resources = new Array<ResourceInfo>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public <T> void add(String fileName, Class<T> resourceType) {
		ResourceInfo resource = new ResourceInfo();
		resource.fileName = fileName;
		resource.resourceType = resourceType;
		
		this.resources.add(resource);
	}
	
	public void remove(String fileName) {
		ResourceInfo resource = new ResourceInfo();
		resource.fileName = fileName;
		
		this.resources.removeValue(resource, false);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Array<ResourceInfo> getResources() {
		return this.resources;
	}
}
