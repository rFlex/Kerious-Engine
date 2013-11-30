/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// SavableData.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 5:23:28 PM
////////

package net.kerious.engine.world;

/**
 * A World object is a part of the world. It can be an Entity, a Player or
 * some manager like an EntityManager
 * @author simoncorsin
 *
 */
public interface WorldObject {

	int getId();
	void setId(int id);
	
	int getType();
	
}
