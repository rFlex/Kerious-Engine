package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.utils.Pool.ObjectCreator;
import com.kerious.framework.world.entities.Entity;

public class EntityRegisterEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////
 
	public static final byte byteIdentifier = 0x1;
	protected int entityID;
	protected int ownerID;
	protected int entityType;
	protected int ownerUserID;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new EntityRegisterEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EntityRegisterEvent() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.entityID);
		rw.write(this.ownerID);
		rw.write(this.entityType);
		rw.write(this.ownerUserID);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.entityID = rw.read(entityID);
		this.ownerID = rw.read(ownerID);
		this.entityType = rw.read(entityType);
		this.ownerUserID = rw.read(ownerUserID);
	}
	
	public static EntityRegisterEvent create() {
		EntityRegisterEvent event = new EntityRegisterEvent();
		
		return event;
	}
	
	public static GameEventCreator creator(final Entity entity) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return EntityRegisterEvent.create(entity);
			}
		};
	}
	
	public static EntityRegisterEvent create(Entity entity) {
		EntityRegisterEvent event = new EntityRegisterEvent();

		final int ownerID = entity.getParentEntity() != null ? entity.getParentEntity().getEntityID() : 0;
		
		event.entityID = entity.getEntityID();
		event.ownerID = ownerID;
		event.entityType = entity.getEntityType();
		event.ownerUserID = entity.getPlayerData() != null ? entity.getPlayerData().getPlayerID() : -1;
		
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

	public int getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public final int getOwnerUserID() {
		return ownerUserID;
	}

	public final void setOwnerUserID(int ownerUserID) {
		this.ownerUserID = ownerUserID;
	}
}
