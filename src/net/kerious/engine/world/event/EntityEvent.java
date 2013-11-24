package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class EntityEvent extends Event {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int entityId;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityEvent(byte eventType) {
		super(eventType);
	}

	////////////////////////
	// METHODS
	////////////////
	
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