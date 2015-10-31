/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// Database.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 5:21:18 PM
////////

package net.kerious.engine.world;

import me.corsin.javatools.misc.NullArgumentException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public final class WorldObjectDatabase {

	////////////////////////
	// VARIABLES
	////////////////
	
	private IntMap<WorldObject> datas;
	private IntMap<Array<WorldObject>> datasByTypes;
	private int sequence;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WorldObjectDatabase() {
		this.datas = new IntMap<WorldObject>();
		this.datasByTypes = new IntMap<Array<WorldObject>>();
		this.sequence = 1;
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Add the WorldObject and attributes an auto-generated id
	 * @param worldObject
	 */
	public void add(WorldObject worldObject) {
		this.add(worldObject, this.sequence++);
	}
	
	/**
	 * Add the WorldObject and set the specified id
	 * @param worldObject
	 * @param id
	 */
	public void add(WorldObject worldObject, int id) {
		if (worldObject == null) {
			throw new NullArgumentException("worldObject");
		}
		WorldObject old = this.datas.put(id, worldObject);
		
		if (old != null) {
			this.removeFromTypes(old);
		}
		
		int type = worldObject.getType();
		
		Array<WorldObject> types = this.datasByTypes.get(type);
		if (types == null) {
			types = new Array<WorldObject>(false, 32, WorldObject.class);
			this.datasByTypes.put(type, types);
		}
		
		types.add(worldObject);
	}
	
	final private void removeFromTypes(WorldObject worldObject) {
		Array<WorldObject> types = this.datasByTypes.get(worldObject.getType());
		
		if (types != null) {
			types.removeValue(worldObject, true);
		}
	}
	
	/**
	 * Remove the WorldObject from the Database
	 * @param worldObject
	 */
	public void remove(WorldObject worldObject) {
		if (worldObject == null) {
			throw new NullArgumentException("worldObject");
		}
		
		this.removeFromTypes(worldObject);
		this.datas.remove(worldObject.getId());
	}
	
	/**
	 * Remove the WorldObject with the id from the database
	 * @param id
	 * @return the removed WorldObject, or null if none was removed
	 */
	public WorldObject remove(int id) {
		WorldObject worldObject = this.datas.remove(id);
		
		if (worldObject != null) {
			this.removeFromTypes(worldObject);
		}
		
		return worldObject;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	/**
	 * Return the WorldObject associated with the id
	 * @param id
	 * @return
	 */
	final public WorldObject get(int id) {
		return this.datas.get(id);
	}
	
	/**
	 * Return the WorldObject's as array with the type
	 * If no world object was created with this type, null
	 * will be returned
	 * @param type
	 * @return
	 */
	final public Array<WorldObject> getByType(int type) {
		return this.datasByTypes.get(type);
	}
}
