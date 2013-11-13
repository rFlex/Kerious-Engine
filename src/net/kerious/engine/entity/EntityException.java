/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// EntityException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 11:40:39 PM
////////

package net.kerious.engine.entity;

public class EntityException extends Exception {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 5355700855881657953L;
	private int entityType;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EntityException(int entityType, String message) {
		super(message);
		
		this.entityType = entityType;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getEntityType() {
		return entityType;
	}

}
