package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class PlayerEvent extends Event {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int playerId;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PlayerEvent(byte eventType) {
		super(eventType);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.playerId = buffer.getInt();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.putInt(this.playerId);
	}
	
	@Override
	public void reset() {
		super.reset();
	
		this.playerId = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
