/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EventManagerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2013 at 7:48:48 PM
////////

package net.kerious.engine.world.event;

public interface EventManagerListener {

	void onEventFired(EventManager eventManager, Event event);
	
}
