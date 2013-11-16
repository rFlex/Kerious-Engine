/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity.model
// EntityModel.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:22:34 PM
////////

package net.kerious.engine.entity.model;

import me.corsin.javatools.misc.PoolableImpl;

public class EntityModel extends PoolableImpl {

	////////////////////////
	// VARIABLES
	////////////////
	
	private int type;
	private int parentId;
	private int id;
	private int skinId;
	private float x;
	private float y;
	private float width;
	private float height;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityModel() {
		
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void reset() {
		this.type = 0;
		this.parentId = 0;
		this.id = 0;
		this.skinId = 0;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getSkinId() {
		return skinId;
	}

	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
}
