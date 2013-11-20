/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.utils
// TemporaryUpdatableArray.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 19, 2013 at 2:19:47 AM
////////

package net.kerious.engine.utils;

import com.badlogic.gdx.utils.SnapshotArray;

public class TemporaryUpdatableArray<T extends TemporaryUpdatable> extends SnapshotArray<T> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public TemporaryUpdatableArray(Class<T> arrayClass) {
		super(arrayClass);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void update(float deltaTime) {
		T[] array = this.begin();
		
		for (int i = 0, length = this.size; i < length; i++) {
			T element = array[i];
			
			if (!element.hasExpired()) {
				element.update(deltaTime);
			} else {
				this.removeValue(element, true);
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
