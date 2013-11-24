package net.kerious.engine.world.event;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class EntityCreatedEvent extends EntityEvent {

	////////////////////////
	// VARIABLES
	////////////////
	
	public byte entityType;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EntityCreatedEvent() {
		super(Events.EntityCreated);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);

		this.entityType = buffer.get();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);

		buffer.put(this.entityType);
	}
	
	@Override
	public void reset() {
		super.reset();

		this.entityType = 0;
	}
	
	public static void createAndFire(EventManager eventManager, int entityId, byte entityType) {
		EntityCreatedEvent entityCreatedEvent = (EntityCreatedEvent)eventManager.createEvent(Events.EntityCreated);

		entityCreatedEvent.entityId = entityId;
		entityCreatedEvent.entityType = entityType;
		
		fire(eventManager, entityCreatedEvent);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
