/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.collisions
// CollisionRegion.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 14, 2012 at 6:50:37 PM
////////

package com.kerious.framework.collisions;

import com.badlogic.gdx.math.Rectangle;

public class CollisionRegion implements ICollisionable {


	////////////////////////
	// VARIABLES
	////////////////

	private float x;
	private float y;
	private float height;
	private float width;
	private Rectangle rectangle;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CollisionRegion(float x, float y, float width, float height)	{
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public boolean isTouched(ICollisionable actor) {
		return false;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public float getX() {
		return x;
	}
	
	@Override
	public float getY() {
		return y;
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	@Override
	public float getWidth() {
		return width;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
}
