/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EntityDestroyedEvent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2013 at 6:44:41 PM
////////

package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class EntityDestroyedEvent extends Event {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int entityId;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityDestroyedEvent() {
		super(Events.EntityDestroyed);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void copyTo(KeriousSerializableData object) {
		super.copyTo(object);
		EntityDestroyedEvent event = (EntityDestroyedEvent)object;
		
		event.entityId = this.entityId;
	}
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.entityId = buffer.getInt();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.putInt(this.entityId);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.entityId = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
