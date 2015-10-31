/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceBundleLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 28, 2013 at 3:36:54 AM
////////

package net.kerious.engine.resource;

import me.corsin.javatools.task.TaskQueue;

import com.badlogic.gdx.utils.Array;

public class ResourceBundleLoader implements ResourceLoader<ResourceBundle> {

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
	public ResourceBundle load(TaskQueue mainTaskQueue, Resource<ResourceBundle> resource, ResourceManager resourceManager) {
		return (ResourceBundle)resource;
	}

	@Override
	public Array<ResourceDescriptor> compileDependencies(Resource<ResourceBundle> resource) {
		return null;
	}

	@Override
	public boolean needsDrawingContext() {
		return false;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
