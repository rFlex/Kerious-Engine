/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.utils
// AsyncLinkedList.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 28, 2012 at 1:40:02 AM
////////

package com.kerious.framework.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class AsyncLinkedList<T> implements Iterable<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private LinkedList<T> list;
	private ArrayList<T> toAdd;
	private ArrayList<T> toRemove;
	private boolean locked;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AsyncLinkedList() {
		this.list = new LinkedList<T>(); 
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
	
	public void remove(T element) {
		if (this.locked) {
			this.toRemove.add(element);
		} else {
			this.list.remove(element);
		}
	}
	
	public void removeFirst() {
		if (this.locked) {
			this.toRemove.add(this.list.getFirst());
		} else {
			this.list.removeFirst();
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
