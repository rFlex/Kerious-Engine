/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// Resource.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:04:49 AM
////////

package net.kerious.engine.resource;

import java.io.Closeable;
import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;

import net.kerious.engine.utils.ReferencableImpl;

public class Resource<T> extends ReferencableImpl 	{

	////////////////////////
	// VARIABLES
	////////////////
	
	public Class<?> resourceType;
	public String fileName;
	public T resource;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void dispose() {
		T resource = this.resource;
		if (resource != null) {
			if (resource instanceof Disposable) {
				((Disposable)resource).dispose();
			} else if (resource instanceof Closeable) {
				try {
					((Closeable)resource).close();
				} catch (IOException e) {
				}
			}
			this.resource = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof Resource)) {
			return false;
		}
		Resource<T> otherRes = (Resource<T>)otherObject;
		
		if ((this.fileName == null) != (otherRes.fileName == null)) {
			return false;
		}
		
		if (this.fileName == null) {
			return true;
		}
		
		return this.fileName.equals(otherRes.fileName);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
