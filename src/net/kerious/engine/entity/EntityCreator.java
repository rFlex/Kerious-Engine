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

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

}
