/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// EntityManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 11:23:06 PM
////////

package net.kerious.engine.entity;

import net.kerious.engine.entity.model.EntityModel;

import com.badlogic.gdx.utils.IntMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityManager {
	
	////////////////////////
	// VARIABLES
	////////////////

	final private IntMap<Entity> entities;
	final private IntMap<EntityCreator> entityCreators;
	private EntityManagerListener listener;
	private int sequence;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityManager() {
		this.entities = new IntMap<Entity>(64);
		this.entityCreators = new IntMap<EntityCreator>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void registerEntity(int entityType, Class<?> entityClass, Class<?> entityModelClass) {
		this.registerEntity(entityType, new ReflectionEntityCreator(entityClass, entityModelClass));
	}
	
	public void registerEntity(int entityType, EntityCreator entityCreator) {
		entityCreator.setEntityType(entityType);
		
		this.entityCreators.put(entityType, entityCreator);
	}
	
	final private EntityCreator getEntityCreator(int entityType) throws EntityException {
		EntityCreator entityCreator = this.entityCreators.get(entityType);
		
		if (entityCreator == null) {
			throw new EntityException(entityType, "No entity creator was registered for entityType " + entityType);
		}
		
		return entityCreator;
	}
	
	final private Entity createEntity(EntityCreator entityCreator, EntityModel entityModel) throws EntityException {
		Entity entity = entityCreator.createEntity(entityModel);
		entity.setEntityManager(this);
		
		this.entities.put(entityModel.getId(), entity);
		
		if (this.listener != null) {
			this.listener.onEntityCreated(entity);
		}
		
		return entity;
	}
	
	public Entity createEntity(int entityType) throws EntityException {
		EntityCreator entityCreator = this.getEntityCreator(entityType);
		
		EntityModel entityModel = entityCreator.createEntityModel();
		entityModel.setId(this.sequence++);
		
		return this.createEntity(entityCreator, entityModel);
	}
	
	public Entity createEntity(EntityModel entityModel) throws EntityException {
		if (entityModel == null) {
			throw new IllegalArgumentException("entityModel may not be null");
		}
		
		int entityType = entityModel.getType();
		
		EntityCreator entityCreator = this.getEntityCreator(entityType);
		
		return this.createEntity(entityCreator, entityModel);
	}
	
	public void destroyEntity(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity may not be null");
		}
		
		EntityModel model = entity.getModel();
		
		if (model == null) {
			throw new IllegalArgumentException("the entity doesn't have any model");
		}
		
		int entityId = model.getId();
		
		this.entities.remove(entityId);
		
		entity.release();
		model.release();
		
		if (this.listener != null) {
			this.listener.onEntityDestroyed(entityId);
		}
	}
	
	public EntityModel createEntityModel(int entityType) throws EntityException {
		EntityCreator entityCreator = this.getEntityCreator(entityType);
		
		return entityCreator.createEntityModel();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Entity getEntity(int entityId) {
		return this.entities.get(entityId);
	}

	public EntityManagerListener getListener() {
		return listener;
	}

	public void setListener(EntityManagerListener listener) {
		this.listener = listener;
	}
	

}
