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
	public int id;
	
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
		this.id = buffer.getInt();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putInt(this.id);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.id = 0;
	}
	
	public int hashCode() {
		return this.id;
	}
	
	protected static void fire(EventManager eventManager, Event event) {
		eventManager.fireEvent(event);
		
		event.release();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
