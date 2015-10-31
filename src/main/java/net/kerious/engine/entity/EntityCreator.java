/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// EntityCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 11:28:02 PM
////////

package net.kerious.engine.entity;

import me.corsin.javatools.misc.Poolable;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.utils.ControllerFactory;

public abstract class EntityCreator<T extends Entity, T2 extends EntityModel>
				extends ControllerFactory<T, EntityModel> {

	////////////////////////
	// VARIABLES
	////////////////
	
	private byte entityType;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityCreator() {

	}

	////////////////////////
	// METHODS
	////////////////
	
	abstract protected T newEntity();
	abstract protected T2 newEntityModel();
	
	protected T newController() {
		return this.newEntity();
	}
	
	protected T2 newModel() {
		return this.newEntityModel();
	}
	
	public T createEntity() {
		return super.createController();
	}

	public T createEntity(T2 entityModel) {
		return super.createController(entityModel);
	}
	
	@SuppressWarnings("unchecked")
	public T2 createEntityModel() {
		T2 entityModel = (T2)super.createModel();
		
		entityModel.type = this.entityType;
		
		return entityModel;
	}
	
	public void preload(int quantity) {
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
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public byte getEntityType() {
		return entityType;
	}

	public void setEntityType(byte entityType) {
		this.entityType = entityType;
	}

}
