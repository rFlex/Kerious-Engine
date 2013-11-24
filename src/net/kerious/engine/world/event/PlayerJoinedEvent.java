package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class PlayerJoinedEvent extends PlayerEvent {

	////////////////////////
	// VARIABLES
	////////////////
	
	public String name;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PlayerJoinedEvent() {
		super(Events.PlayerJoined);
	}

	////////////////////////
	// METHODS
	////////////////
	
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);

		this.name = this.getString(buffer);
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);

		this.putString(buffer, this.name);
	}
	
	@Override
	public void reset() {
		super.reset();

		this.name = null;
	}
	
	public static void createAndFire(EventManager eventManager, int playerId) {
		PlayerJoinedEvent playerJoinedEvent = (PlayerJoinedEvent)eventManager.createEvent(Events.PlayerJoined);
		
		playerJoinedEvent.playerId = playerId;
		
		fire(eventManager, playerJoinedEvent);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
