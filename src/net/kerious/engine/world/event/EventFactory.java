/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EventFactory.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 20, 2013 at 9:00:55 PM
////////

package net.kerious.engine.world.event;

import me.corsin.javatools.misc.Pool;

import com.badlogic.gdx.utils.IntMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EventFactory {

	////////////////////////
	// VARIABLES
	////////////////
	
	private IntMap<Pool<Event>> eventsType;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EventFactory() {
	
	}

	////////////////////////
	// METHODS
	////////////////
	
	public <T extends Event> void registerEventType(byte eventType, Pool<T> pool) {
		this.eventsType.put((int)eventType, (Pool)pool);
	}
	
	public void unregisterEventType(byte eventType) {
		this.eventsType.remove(eventType);
	}
	
//	public KeriousSerializableData create
	
//	public Event createEvent() {
//		Event event = this.eventsPool.obtain();
//		
//		return event;
//	}
//	
//	public Event createEvent(byte eventType) {
//		Event event = this.createEvent();
//		
//		return event;
//	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
