/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity.model
// EntityModelCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 1:35:58 PM
////////

package net.kerious.engine.entity.model;

import net.kerious.engine.entity.EntityException;

public interface EntityModelCreator {
	
	EntityModel createEntityModel(byte entityType) throws EntityException;

}
