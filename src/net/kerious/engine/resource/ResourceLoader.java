/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceLoader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:08:45 AM
////////

package net.kerious.engine.resource;

import com.badlogic.gdx.files.FileHandle;

import net.kerious.engine.KeriousException;
import me.corsin.javatools.task.TaskQueue;

public interface ResourceLoader<T> {

	/**
	 * Load the resource
	 * @param mainTaskQueue If null, the load operation is already in the mainTaskQueue. Otherwise
	 * the mainTaskQueue is provided in case of the loader needs to access the openGL context for instance
	 * @param resource the resource to load
	 * @return the loaded resource
	 * @throws KeriousException
	 */
	T load(TaskQueue mainTaskQueue, FileHandle file) throws KeriousException;
	
}
