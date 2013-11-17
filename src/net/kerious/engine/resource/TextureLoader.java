/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// TextureLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 12:45:10 PM
////////

package net.kerious.engine.resource;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import me.corsin.javatools.task.TaskQueue;
import net.kerious.engine.KeriousException;

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
	public Texture load(TaskQueue mainTaskQueue, FileHandle file)
			throws KeriousException {
		if (mainTaskQueue == null) {
			loadedTexture = new Texture(file); 
		} else {
			final Pixmap pixmap = new Pixmap(file);
			
			mainTaskQueue.executeSync(new Runnable() {
				@Override
				public void run() {
					loadedTexture = new Texture(pixmap);
				}
			});
		}
		
		return loadedTexture;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
