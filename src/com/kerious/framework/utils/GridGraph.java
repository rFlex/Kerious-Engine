/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.utils
// GridGraph.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 31, 2013 at 6:17:46 PM
////////

package com.kerious.framework.utils;

import java.util.LinkedList;

import com.badlogic.gdx.math.Rectangle;

public class GridGraph<T extends IRectangle> {

	////////////////////////
	// VARIABLES
	////////////////

	final ExtensibleArray<ExtensibleArray<LinkedList<T>>> graph;
	final private float tileSize;
	final private Rectangle tmpRectangle;

	////////////////////////
	// NESTED CLASSES
	////////////////

	private static interface Action<T> {
		void act(LinkedList<T> collisions);
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public GridGraph(float tileSize) {
		this.tileSize = tileSize;
		this.tmpRectangle = new Rectangle();
		this.graph = new ExtensibleArray<ExtensibleArray<LinkedList<T>>>() {

			@Override
			protected ExtensibleArray<LinkedList<T>> createNode() {
				return new ExtensibleArray<LinkedList<T>>() {

					@Override
					protected LinkedList<T> createNode() {
						return new LinkedList<T>();
					}
				};
			}
			
		};
	}

	////////////////////////
	// METHODS
	////////////////

	public void addRegion(final T region) {
		this.foreach(region, new Action<T>() {

			@Override
			public void act(LinkedList<T> collisions) {
				collisions.add(region);
			}
		});
	}

	public void removeRegion(final T region) {
		this.foreach(region, new Action<T>() {

			@Override
			public void act(LinkedList<T> collisions) {
				collisions.remove(region);
			}
		});
	}

	private final void foreach(T collision, Action<T> toDo) {
		// Adding one prevent using the >= operator which is slower on flaot than the > operator
		final float maxX = collision.getX() + collision.getWidth() + 1; 
		final float maxY = collision.getY() + collision.getHeight() + 1;

		for (float x = collision.getX(); x < maxX; x += this.tileSize) {
			for (float y = collision.getY(); y < maxY; y += this.tileSize) {
				int posX = (int)(x / this.tileSize);
				int posY = (int)(y / this.tileSize);

				toDo.act(this.graph.get(posY).get(posX));
			}
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final T getNodeForPosition(float x, float y) {
		int posX = (int)(x / this.tileSize);
		int posY = (int)(y / this.tileSize);
		
		LinkedList<T> list = this.graph.get(posY).get(posX);
		
		for (T elem : list) {
			this.tmpRectangle.set(elem.getX(), elem.getY(), elem.getWidth(), elem.getHeight());
			if (this.tmpRectangle.contains(x, y)) {
				return elem;
			}
		}
		
		return null;
	}

}

