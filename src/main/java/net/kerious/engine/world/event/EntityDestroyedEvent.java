/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EntityDestroyedEvent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2013 at 6:44:41 PM
////////

package net.kerious.engine.world.event;

public class EntityDestroyedEvent extends EntityEvent {

	////////////////////////
	// VARIABLES
	////////////////
	

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityDestroyedEvent() {
		super(Events.EntityDestroyed);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public static void createAndFire(EventManager eventManager, int entityId) {
		EntityDestroyedEvent entityCreatedEvent = (EntityDestroyedEvent)eventManager.createEvent(Events.EntityDestroyed);

		entityCreatedEvent.entityId = entityId;
		
		fire(eventManager, entityCreatedEvent);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
