package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.utils.Pool.ObjectCreator;
import com.kerious.framework.world.entities.Entity;

public class EntityUnregisterEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////
 
	public static final byte byteIdentifier = 0x2;
	protected int entityID;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new EntityUnregisterEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	private EntityUnregisterEvent() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.entityID);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.entityID = rw.read(entityID);
	}
	
	public static GameEventCreator creator(final Entity entity) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return EntityUnregisterEvent.create(entity);
			}
		};
	}
	
	public static EntityUnregisterEvent create(Entity entity) {
		EntityUnregisterEvent event = new EntityUnregisterEvent();
		
		event.entityID = entity.getEntityID();
		
		return event;
	}
	
	public static EntityUnregisterEvent create(int entityID) {
		EntityUnregisterEvent event = new EntityUnregisterEvent();
		
		event.entityID = entityID;
		
		return event;
	}
	
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getEntityID() {
		return entityID;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}
}
