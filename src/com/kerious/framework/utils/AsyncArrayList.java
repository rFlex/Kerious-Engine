/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.utils
// AsyncArrayList.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 9, 2012 at 1:02:55 AM
////////

package com.kerious.framework.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class AsyncArrayList<T> implements Iterable<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private ArrayList<T> list;
	private ArrayList<T> toAdd;
	private ArrayList<T> toRemove;
	private boolean locked;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AsyncArrayList() {
		this.list = new ArrayList<T>(); 
		this.toAdd = new ArrayList<T>();
		this.toRemove = new ArrayList<T>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void add(T element) {
		if (this.locked) {
			this.toAdd.add(element);
		} else {
			this.list.add(element);
		}
	}

	public boolean remove(T element) {
		if (this.locked) {
			this.toRemove.add(element);
			
			return this.list.contains(element);
		} else {
			return this.list.remove(element);
		}
	}

	public void lock() {
		this.locked = true;
	}

	public void unlock() {
		for (int i = 0; i < this.toAdd.size(); i++) {
			this.list.add(this.toAdd.get(i));
		}
		this.toAdd.clear();

		for (int i = 0; i < this.toRemove.size(); i++) {
			this.list.remove(this.toRemove.get(i));
		}
		this.toRemove.clear();

		this.locked = false;
	}

	@Override
	public Iterator<T> iterator() {
		return this.list.iterator();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int size() {
		return this.list.size();
	}
}
