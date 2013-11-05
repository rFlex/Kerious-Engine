/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.entities
// EntityFactory.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 13, 2012 at 5:54:58 PM
////////

package com.kerious.framework.world.entities;

import java.util.ArrayList;
import java.util.List;

import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.utils.EventListenerHolder;
import com.kerious.framework.utils.Pool;
import com.kerious.framework.world.GameWorld;

public class EntityFactory {

	////////////////////////
	// VARIABLES
	////////////////

	public final EventListenerHolder<Entity> onEntityRegistered = new EventListenerHolder<Entity>();
	public final EventListenerHolder<Entity> onEntityUnregistered = new EventListenerHolder<Entity>();
	private List<EntityTypeHandle> _entityTypes;
	private GameWorld _gameWorld;
	private EntityHandler _entityHandler;
	private boolean _allowAutoRegistering;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	private class EntityTypeHandle {
		
		final public Class<?> entityClass;
		private Pool<Entity> pool;
		private Entity creator;
		
		public EntityTypeHandle(Entity creator) {
			this.entityClass = creator.getClass();
			this.pool = new Pool<Entity>();
			this.creator = creator;
		}
		
		public Entity create() {
			Entity entity = pool.obtain();
			
			if (entity == null) {
				entity = this.creator.clone(_gameWorld.getApplication().drawingEnabled);
				entity.setEntityType(this.creator.getEntityType());
			}
			
			return entity;
		}
		
		public void release(Entity entity) {
			this.pool.release(entity);
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EntityFactory(GameWorld gameWorld, boolean allowAutoRegistering) {
		this._entityTypes = new ArrayList<EntityTypeHandle>();
		this._gameWorld = gameWorld;
		this._entityHandler = new EntityHandler();
		this._allowAutoRegistering = allowAutoRegistering;
	}
	
	////////////////////////
	// METHODS
	////////////////

	public <T extends Entity> void addEntity(T entity) {
		entity.setEntityType(this._entityTypes.size());
		
		this._entityTypes.add(new EntityTypeHandle(entity));
	}
	
	private void initEntity(Entity entity, Entity owner, PlayerData ownerUser) {
		entity.init();
		entity.addToWorld(owner, ownerUser);
		entity.setParentEntity(owner);
		entity.setPlayerData(ownerUser);
	}
	
	private EntityTypeHandle getCreator(int entityType) {
		for (EntityTypeHandle creator : this._entityTypes) {
			if (creator.creator.getEntityType() == entityType) {
				return creator;
			}
		}
		throw new KeriousException("Invalid entity type " + entityType);
	}
	
	private EntityTypeHandle getCreator(Class<?> entityType) {
		for (EntityTypeHandle creator : this._entityTypes) {
			if (creator.entityClass == entityType) {
				return creator;
			}
		}
		throw new KeriousException("Invalid entity type " + entityType.getSimpleName());
	}
	
	public <T extends Entity> T spawnEntity(Class<T> entityType, PlayerData owner) {
		return this.spawnEntity(entityType, null, owner);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Entity> T spawnEntity(Class<T> entityType, Entity ownerEntity, PlayerData owner) {
		T entity = (T)getCreator(entityType).create();
		
		if (this._allowAutoRegistering) {
			this._entityHandler.registerEntity(entity, ownerEntity);
		}
		entity.setRegistered(this._allowAutoRegistering);
		
		this.initEntity(entity, ownerEntity, owner);
		
		if (this._allowAutoRegistering) {
			this.onEntityRegistered.call(this, entity);
		}
		
		return entity;
	}
	
	public Entity spawnEntity(int entityType, int entityID, int ownerEntityID, PlayerData owner) {
		Entity entity = null;
		
		if (entityType >= 0 && entityType < this._entityTypes.size()) {
			entity = this._entityTypes.get(entityType).create();
			final Entity entityOwner = this._entityHandler.getEntityByID(ownerEntityID);
			
			this._entityHandler.registerEntity(entity, entityID);
			this.initEntity(entity, entityOwner, owner);
			
			entity.setRegistered(true);
			
			this.onEntityRegistered.call(this, entity);
		}
		
		return entity;
	}
	
	
	public void destroyEntity(Entity entity) {
		if (entity.isRegistered()) {
			this.onEntityUnregistered.call(this, entity);
			
			this._entityHandler.unregisterEntity(entity);
		}
		
		entity.removeFromWorld();
		this.getCreator(entity.getEntityType()).release(entity);
	}
	
	public void destroyEntity(int entityID) {
		final Entity entity = this._entityHandler.getEntityByID(entityID);
		
		if (entity != null) {
			this.destroyEntity(entity);
		}
	}
	
	public void destroyAllEntities() {
		List<Entity> list = new ArrayList<Entity>(this._entityHandler.getEntities());
		
		for (Entity entity : list) {
			this.destroyEntity(entity);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public EntityHandler getEntityHandler() {
		return this._entityHandler;
	}
}
