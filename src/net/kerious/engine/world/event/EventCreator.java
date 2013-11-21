/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EventCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 1:35:22 PM
////////

package net.kerious.engine.world.event;

public interface EventCreator {
	
	Event createEvent(byte eventType);

}
