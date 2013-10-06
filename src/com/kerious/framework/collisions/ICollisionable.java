/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.collisions
// ICollisionable.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 14, 2012 at 6:15:17 PM
////////

package com.kerious.framework.collisions;

public interface ICollisionable {
	
	public boolean isTouched(ICollisionable collisionable);
	public float getX();
	public float getY();
	public float getHeight();
	public float getWidth();
	
}
