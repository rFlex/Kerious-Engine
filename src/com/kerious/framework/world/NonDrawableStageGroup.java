/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.world
// NonDrawableStageGroup.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 7, 2012 at 9:01:23 PM
////////

package com.kerious.framework.world;

public class NonDrawableStageGroup extends StageGroup {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public NonDrawableStageGroup() {
		super(0, 0, null, true);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void draw() {
		this.drawChildren();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
