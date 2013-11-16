/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// EntityCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 11:28:02 PM
////////

package net.kerious.engine.entity;

import net.kerious.engine.entity.model.EntityModel;
import me.corsin.javatools.misc.Pool;
import me.corsin.javatools.misc.Poolable;

public abstract class EntityCreator<T extends Entity<T2, ?>, T2 extends EntityModel> {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private Pool<T> entitiesPool;
	final private Pool<T2> entityModelsPool;
	private int entityType;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityCreator() {
		this.entitiesPool = new Pool<T>() {
			@Override
			protected T instantiate() {
				return newEntity();
			}
		};
		this.entityModelsPool = new Pool<T2>() {
			@Override
			protected T2 instantiate() {
				return newEntityModel();
			}
		};
	}

	////////////////////////
	// METHODS
	////////////////
	
	abstract protected T newEntity();
	abstract protected T2 newEntityModel();

	public T createEntity(T2 entityModel) throws EntityException {
		T entity = this.entitiesPool.obtain();
		
		if (entity == null) {
			throw new EntityException(entityType, "No entity was created from the EntityCreator");
		}
		
		entity.setModel(entityModel);
		entity.buildFromModel();
		
		return entity;
	}
	
	public T2 createEntityModel() throws EntityException {
		T2 entityModel = this.entityModelsPool.obtain();
		
		if (entityModel == null) {
			throw new EntityException(this.entityType, "No entity model was created from the EntityCreator");
		}
		
		entityModel.setType(this.entityType);
		
		return entityModel;
	}
	
	public void preload(int quantity) throws EntityException {
		Poolable[] entities = new Poolable[quantity];
		Poolable[] models = new Poolable[quantity];
		
		for (int i = 0; i < quantity; i++) {
			T2 model = this.createEntityModel();
			T entity = this.createEntity(model);
			
			entities[i] = entity;
			models[i] = model;
		}
		
		for (int i = 0; i < quantity; i++) {
			entities[i].release();
			models[i].release();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public Pool<T> getEntitiesPool() {
		return entitiesPool;
	}

	public Pool<T2> getEntityModelsPool() {
		return entityModelsPool;
	}

}
