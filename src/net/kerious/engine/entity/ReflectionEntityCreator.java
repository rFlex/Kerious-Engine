/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// ReflectionEntityCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 11:33:30 PM
////////

package net.kerious.engine.entity;

import net.kerious.engine.entity.model.EntityModel;

public class ReflectionEntityCreator<EntityType extends Entity, EntityModelType extends EntityModel> extends EntityCreator<EntityType, EntityModelType> {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private Class<EntityType> entityClass;
	final private Class<EntityModelType> entityModelClass;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ReflectionEntityCreator(Class<EntityType> entityClass, Class<EntityModelType> entityModelClass) {
		this.entityClass = entityClass;
		this.entityModelClass = entityModelClass;
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	protected EntityType newEntity() {
		try {
			return this.entityClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected EntityModelType newEntityModel() {
		try {
			return this.entityModelClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
