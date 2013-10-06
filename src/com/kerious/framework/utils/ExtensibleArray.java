/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.utils
// ExtensibleArray.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 31, 2013 at 6:48:19 PM
////////

package com.kerious.framework.utils;

import java.util.ArrayList;

public abstract class ExtensibleArray<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private ArrayList<T> positiveArray;
	private ArrayList<T> negativeArray;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ExtensibleArray() {
		this.positiveArray = new ArrayList<T>();
		this.negativeArray = new ArrayList<T>();
	}

	////////////////////////
	// METHODS
	////////////////

	protected abstract T createNode();
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	private final T get(int index, ArrayList<T> array) {
		while (index >= array.size()) {
			array.add(this.createNode());
		}
		
		return array.get(index);
	}
	
	public final T get(int index) {
		if (index < 0) {
			return this.get((index + 1) * -1, this.negativeArray);
		} else {
			return this.get(index, this.positiveArray);
		}
	}
}
