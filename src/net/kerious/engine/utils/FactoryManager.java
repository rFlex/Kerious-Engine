/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.utils
// IntFactory.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 20, 2013 at 9:14:47 PM
////////

package net.kerious.engine.utils;

import net.kerious.engine.KeriousException;
import me.corsin.javatools.misc.Pool;

import com.badlogic.gdx.utils.IntMap;

@SuppressWarnings("rawtypes")
public class FactoryManager {

	////////////////////////
	// VARIABLES
	////////////////
	
	private IntMap<Pool> factories;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public FactoryManager() {
		this.factories = new IntMap<Pool>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected void registerFactory(int key, Pool factory) {
		this.factories.put(key, factory);
	}
	
	protected Object createObject(int key) {
		Pool pool = this.factories.get(key);
		
		if (pool != null) {
			return pool.obtain();
		} else {
			throw new KeriousException("No such object with key " + key);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
