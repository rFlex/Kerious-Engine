/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// WordListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 8:48:44 PM
////////

package net.kerious.engine.world;

import net.kerious.engine.entity.Entity;

@SuppressWarnings("rawtypes")
public interface WorldListener {
	
	void willUpdateWorld(World world);
	void didUpdateWorld(World world);
	void onEntityCreated(World world, Entity entity);
	void onEntityDestroyed(World world, int entityId);
	
}
