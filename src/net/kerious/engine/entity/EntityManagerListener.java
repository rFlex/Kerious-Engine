/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// EntityManagerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 3:15:08 PM
////////

package net.kerious.engine.entity;

public interface EntityManagerListener {
	
	void onEntityCreated(Entity entity);
	void onEntityDestroyed(int entityId);

}
