/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.utils
// ReferencableImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:18:21 AM
////////

package net.kerious.engine.utils;

public abstract class ReferencableImpl implements Referencable {

	////////////////////////
	// VARIABLES
	////////////////

	private int retainCount = 1;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void retain() {
		this.retainCount++;
	}

	@Override
	public void release() {
		this.retainCount--;
		if (this.retainCount == 0) {
			this.dispose();
		}
	}

	@Override
	public int getRetainCount() {
		return this.retainCount;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
