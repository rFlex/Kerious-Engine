/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceBundle.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:03:48 AM
////////

package net.kerious.engine.resource;

import me.corsin.javatools.string.StringUtils;

import com.badlogic.gdx.files.FileHandle;

public class ResourceBundle extends Resource<ResourceBundle> {

	////////////////////////
	// VARIABLES
	////////////////
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ResourceBundle() {
		super(ResourceBundle.class, new FileHandle(StringUtils.randomAlphaNumericString(12)));
	}

	////////////////////////
	// METHODS
	////////////////

	/**
	 * Add a resource to load in the Bundle
	 * @param resourceType
	 * @param fileName
	 */
	public void add(Class<?> resourceType, String fileName) {
		this.addDependency(resourceType, fileName);
	}
	
	/**
	 * Remove a resource to load from the Bundle
	 * @param resourceType
	 * @param fileName
	 */
	public void remove(String fileName) {
		this.removeDependency(fileName);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
