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
import net.kerious.engine.utils.FactoryManager;

public class EventFactory extends FactoryManager implements EventCreator {

	////////////////////////
	// VARIABLES
	////////////////
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EventFactory() {
	
	}

	////////////////////////
	// METHODS
	////////////////
	
	public <T extends Event> void registerEventType(byte eventType, Pool<T> pool) {
		this.registerFactory(eventType, pool);
	}
	
	public void unregisterEventType(byte eventType) {
		this.unregisterFactory(eventType);
	}

	@Override
	public Event createEvent(byte eventType) {
		return (Event)this.createObject(eventType);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
