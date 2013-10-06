/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.entities
// Pool.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 20, 2012 at 10:00:37 PM
////////

package com.kerious.framework.utils;

import java.util.ArrayList;

public class Pool<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private Class<?> _managedObjectClass;
	private ArrayList<T> _objects;
	private ObjectCreator<T> _creator;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static interface ObjectCreator<T> {
		T instanciate();
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Pool() {
		this(null);
	}
	
	public Pool(ObjectCreator<T> objectCreator) {
		this._creator = objectCreator;
		this._objects = new ArrayList<T>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public final T obtain() {
		T obj = null;
		
		if (!this._objects.isEmpty()) {
			obj = this._objects.get(this._objects.size() - 1);
			this._objects.remove(this._objects.size() - 1);
		} else if (this._creator != null) {
			obj = this._creator.instanciate();
		}
		
		return obj;
	}
	
	public final void release(T obj) {
		this._objects.add(obj);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final void setCreator(ObjectCreator<T> objectCreator) {
		this._creator = objectCreator;
	}
	
	protected final void setManagedObjectClass(Class<?> managedObject) {
		this._managedObjectClass = managedObject;
	}
	
	public final Class<?> getManagedObjectClass() {
		return this._managedObjectClass;
	}
	
	public final int getRetainedObjectsSize() {
		return this._objects.size();
	}
	
	

}
