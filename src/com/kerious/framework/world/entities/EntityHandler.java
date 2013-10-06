/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network
// EntityHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 13, 2012 at 5:45:54 PM
////////

package com.kerious.framework.world.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.kerious.framework.exceptions.KeriousException;

public class EntityHandler implements Iterable<Entity> {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<Integer, Entity> _entities;
	private int currentIdentifier;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EntityHandler() {
		this._entities = new HashMap<Integer, Entity>();
		this.currentIdentifier = 10;
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void registerEntity(Entity entity, int entityID) {
		entity.setEntityID(entityID);
		this._entities.put(entityID, entity);
	}
	
	public void registerEntity(Entity entity, Entity owner) {
		entity.setEntityID(this.currentIdentifier);
		this._entities.put(this.currentIdentifier, entity);
		this.currentIdentifier++;
	}
	
	public void unregisterEntity(Entity entity) {
		final int entityID = entity.getEntityID();
		if (entityID > 0) {
			this._entities.remove(entityID);
			entity.setEntityID(-1);
		} else {
			throw new KeriousException("Attempted to remove an entity which was already removed");
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Entity getEntityByID(int entityID) {
		final Entity entity = entityID > 0 ? this._entities.get(entityID) : null;
		
		return entity;
	}

	@Override
	public Iterator<Entity> iterator() {
		return this._entities.values().iterator();
	}
	
	public Collection<Entity> getEntities() {
		return this._entities.values();
	}
	
}
