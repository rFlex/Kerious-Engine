/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.utils
// Size.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 5, 2013 at 5:28:15 PM
////////

package net.kerious.engine.utils;

public class Size {

	////////////////////////
	// VARIABLES
	////////////////
	
	public float width;
	public float height;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Size() {
		
	}
	
	public Size(float width, float height) {
		this.width = width;
		this.height = height;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public Size add(Size oth) {
		this.width += oth.width;
		this.height += oth.height;
		
		return this;
	}
	
	public Size sub(Size oth) {
		this.width -= oth.width;
		this.height -= oth.height;
		
		return this;
	}
	
	public int compareTo(Size size) {
		return Double.compare(this.getSquareSize(), size.getSquareSize());
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public double getSquareSize() {
		return Math.sqrt(this.width * this.width + this.height * this.height);
	}
	
	public void set(Size size) {
		this.width = size.width;
		this.height = size.height;
	}

	public void set(float width, float height) {
		this.width = width;
		this.height = height;
	}
}
