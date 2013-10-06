/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network
// IClientsCommunicator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 14, 2012 at 5:32:37 PM
////////

package com.kerious.framework.events;

import com.kerious.framework.events.GameEvent.GameEventCreator;

public interface IEventPropagator {
	
	void fireEvent(GameEventCreator event);
	
}
