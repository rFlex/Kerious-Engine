/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.events
// MapEvent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2012 at 6:21:52 PM
////////

package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class WorldLoadEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x5;
	private String mapName;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new WorldLoadEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public WorldLoadEvent() {
		super(byteIdentifier);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.mapName);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.mapName = rw.read(mapName);
	}
	
	public static GameEventCreator creator(final String mapName) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return WorldLoadEvent.create(mapName);
			}
		};
	}
	
	public static WorldLoadEvent create(String mapName) {
		WorldLoadEvent event = new WorldLoadEvent();

		event.mapName = mapName;
		
		return event;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final String getMapName() {
		return mapName;
	}

	public final void setMapName(String mapName) {
		this.mapName = mapName;
	}

}
