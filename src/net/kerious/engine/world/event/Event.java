/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// GameEvent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 20, 2013 at 8:44:31 PM
////////

package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public abstract class Event extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////
	
	final public byte type;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Event(byte type) {
		this.type = type;
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		
	}
	
	@Override
	public void reset() {
		super.reset();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
