/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework
// KeriousObjectFactory.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 24, 2012 at 11:07:00 AM
////////

package com.kerious.framework;

import com.kerious.framework.network.protocol.packets.EntityState;
import com.kerious.framework.utils.Pool.ObjectCreator;
import com.kerious.framework.world.entities.PlayerData;

public class KeriousObjectFactory {

	////////////////////////
	// VARIABLES
	////////////////

	private static ObjectCreator<EntityState> entityStateCreator;
	private static ObjectCreator<PlayerData> playerDataCreator;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	public static EntityState createEntityState() {
		init();
		return entityStateCreator.instanciate();
	}
	
	public static PlayerData createPlayerData() {
		init();
		return playerDataCreator.instanciate();
	}
	
	public static void init() {
		if (entityStateCreator == null) {
			setEntityStateCreator(null);
		}
		if (playerDataCreator == null) {
			setPlayerDataCreator(null);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public static void setEntityStateCreator(ObjectCreator<EntityState> creator) {
		if (creator == null) {
			creator = new ObjectCreator<EntityState>() {

				@Override
				public EntityState instanciate() {
					// Default implementation
					return new EntityState();
				}
			};
		}
		
		entityStateCreator = creator;
	}
	
	public static void setPlayerDataCreator(ObjectCreator<PlayerData> creator) {
		if (creator == null) {
			creator = new ObjectCreator<PlayerData>() {

				@Override
				public PlayerData instanciate() {
					// Default implementation
					return new PlayerData();
				}
			};
		}
		
		playerDataCreator = creator;
	}
	
	public static ObjectCreator<EntityState> getEntityStateCreator() {
		init();
		return entityStateCreator;
	}
	
	public static ObjectCreator<PlayerData> getPlayerDataCreator() {
		init();
		return playerDataCreator;
	}
}
