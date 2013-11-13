/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// World.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:32:23 PM
////////

package net.kerious.engine.world;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.controllers.ViewController;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.skin.SkinManager;
import net.kerious.engine.utils.TemporaryUpdatable;

import com.badlogic.gdx.utils.SnapshotArray;

@SuppressWarnings("rawtypes")
public class World extends ViewController implements TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private SnapshotArray<Entity> entities;
	final private EntityManager entityManager;
	final private SkinManager skinManager;
	final private boolean renderingEnabled;
	final private boolean hasAuthority;
	private boolean addedToEngine;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public World(KeriousEngine engine, boolean renderingEnabled, boolean hasAuthority) {
		super(engine);
		
		this.renderingEnabled = renderingEnabled;
		this.hasAuthority = hasAuthority;
		
		this.entities = new SnapshotArray<Entity>(true, 64, Entity.class);
		this.skinManager = new SkinManager(hasAuthority);
		this.entityManager = new EntityManager();
	}

	////////////////////////
	// METHODS
	////////////////

	public void beginReceiveUpdates() {
		if (!this.addedToEngine) {
			this.addedToEngine = true;
			this.getEngine().addTemporaryUpdatable(this);
		}
	}
	
	public void endReceiveUpdates() {
		if (this.addedToEngine) {
			this.addedToEngine = false;
			this.getEngine().removeTemporaryUpdatable(this);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		Entity[] entities = this.entities.begin();
		for (int i = 0, length = this.entities.size; i < length; i++) {
			Entity entity = entities[i];
			
			if (!entity.hasExpired()) {
				entity.update(deltaTime);
			} else {
				this.entities.removeIndex(i);
				entity.setWorld(null);
				entity.removedFromWorld();
			}
		}
		this.entities.end();
	}
	
	public Entity createEntity(int entityType) throws EntityException {
		Entity entity = this.entityManager.createEntity(entityType);
		
		this.addEntity(entity);
		
		return entity;
	}
	
	public void addEntity(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity may not be null");
		}
		
		this.entities.add(entity);
		
		entity.setWorld(this);
		entity.addedToWorld();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public boolean hasExpired() {
		// It never expires. It needs to be force removed
		return false;
	}
	
	public boolean hasAuthority() {
		return this.hasAuthority;
	}
	
	public SkinManager getSkinManager() {
		return this.skinManager;
	}

	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
