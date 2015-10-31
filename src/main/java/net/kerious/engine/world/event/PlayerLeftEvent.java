package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class PlayerLeftEvent extends PlayerEvent {

	////////////////////////
	// VARIABLES
	////////////////
	
	public String reason;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PlayerLeftEvent() {
		super(Events.PlayerLeft);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.reason = getString(buffer);
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);

		putString(buffer, this.reason);
	}
	
	@Override
	public void reset() {
		super.reset();

		this.reason = null;
	}

	public static void createAndFire(EventManager eventManager, int playerId, String reason) {
		PlayerLeftEvent playerLeftEvent = (PlayerLeftEvent)eventManager.createEvent(Events.PlayerJoined);
		
		playerLeftEvent.playerId = playerId;
		playerLeftEvent.reason = reason;
		
		fire(eventManager, playerLeftEvent);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
