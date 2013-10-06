/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world
// GameWorld.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 16 ao�t 2012 at 12:28:32
////////

package com.kerious.framework.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.utils.AsyncArrayList;

public class StageGroup extends Stage {

	////////////////////////
	// VARIABLES
	////////////////
	
	protected StageGroup parent;
	protected AsyncArrayList<StageGroup> children;
	protected boolean enableDrawing;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public StageGroup(float width, float height, SpriteBatch batch) {
		this(width, height, batch, true);
	}
	
	public StageGroup(float width, float height, SpriteBatch batch, boolean enableRendering) {
		super(width, height, false, batch);
		
		this.children = new AsyncArrayList<StageGroup>();
		this.enableDrawing = enableRendering;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void addStage(StageGroup world) {
		world.removeFromSuperStage();
		
		children.add(world);
		
		world.parent = this;
	}
	
	public void removeStage(StageGroup world) {
		if (this.children.remove(world)) {
			world.parent = null;
		}
	}
	
	@Override
	public void act(float delta) {
		if (this.enableDrawing) {
			super.act(delta);
		} else {
			this.getRoot().act(delta);
		}
		
		this.children.lock();
		for (StageGroup world : children) {
			world.act(delta);
		}
		this.children.unlock();
	}
	
	public final void drawWithoutChildren() {
		super.draw();
	}
	
	public final void drawChildren() {
		for (StageGroup world : children) {
			world.draw();
		}
	}
	
	@Override
	public void draw() {
		if (!this.enableDrawing) {
			throw new KeriousException("Attempted to draw a stage which has no drawing context available.");
		}
		
		this.drawWithoutChildren();
		this.drawChildren();
	}
	
	public void removeFromSuperStage() {
		if (this.parent != null) {
			this.parent.removeStage(this);
			this.parent = null;
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public StageGroup getParent() {
		return this.parent;
	}
}
