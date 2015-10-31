/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// TextureLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 12:45:10 PM
////////

package net.kerious.engine.resource;

import me.corsin.javatools.task.TaskQueue;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class TextureLoader implements ResourceLoader<Texture> {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Texture loadedTexture;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Texture load(TaskQueue mainTaskQueue, Resource<Texture> resource, ResourceManager resourceManager) throws Exception {
		if (mainTaskQueue == null) {
			loadedTexture = new Texture(resource.getResourceDescriptor().getFileHandle());
		} else {
			final Pixmap pixmap = new Pixmap(resource.getResourceDescriptor().getFileHandle());
			
			mainTaskQueue.executeSync(new Runnable() {
				@Override
				public void run() {
					loadedTexture = new Texture(pixmap);
				}
			});
		}
		
		return loadedTexture;
	}

	@Override
	public Array<ResourceDescriptor> compileDependencies(Resource<Texture> resource) {
		return null;
	}

	@Override
	public boolean needsDrawingContext() {
		return true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
