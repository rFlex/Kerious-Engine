package com.kerious.framework.utils;

import java.util.ArrayList;

public class EventListenerHolder<T> {

	////////////////////////
	// VARIABLES
	////////////////

	private ArrayList<IEventListener<T>> eventListeners;
	private ArrayList<IEventListener<T>> toRemoveListeners;
	private boolean iterating;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EventListenerHolder() {
		this.eventListeners = new ArrayList<IEventListener<T>>();
		this.toRemoveListeners = new ArrayList<IEventListener<T>>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void addListener(IEventListener<T> eventListener) {
		this.eventListeners.add(eventListener);
	}
	
	public boolean removeListener(IEventListener<T> eventListener) {
		if (this.iterating) {
			this.toRemoveListeners.add(eventListener);
			return true;
		} else {
			return this.eventListeners.remove(eventListener);
		}
		
	}
	
	public void call(Object sender, T arg) {
		this.iterating = true;
		for (IEventListener<T> eventListener : this.eventListeners) {
			eventListener.onFired(sender, arg);
		}
		this.iterating = false;
		
		if (!this.toRemoveListeners.isEmpty()) {
			for (IEventListener<T> eventListener : this.toRemoveListeners) {
				this.eventListeners.remove(eventListener);
			}
			this.toRemoveListeners.clear();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean hasListeners() {
		return !this.eventListeners.isEmpty();
	}
}
