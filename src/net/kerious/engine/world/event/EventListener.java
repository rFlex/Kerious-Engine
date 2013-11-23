/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EventListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2013 at 7:23:11 PM
////////

package net.kerious.engine.world.event;

public interface EventListener {

	void onEventFired(EventManager eventManager, Event event);
	
}
