/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.client
// PlayerStatsManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 7, 2012 at 9:05:42 PM
////////

package com.kerious.framework.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.kerious.framework.KeriousObjectFactory;
import com.kerious.framework.world.entities.PlayerData;

public class PlayersManager implements Iterable<PlayerData> {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<Integer, PlayerData> playersStats;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PlayersManager() {
		this.playersStats = new HashMap<Integer, PlayerData>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public Iterator<PlayerData> iterator() {
		return this.playersStats.values().iterator();
	}
	
	public void addPlayer(int playerID, String playerName) {
		this.playersStats.remove(playerID);
		
		final PlayerData playerData = KeriousObjectFactory.createPlayerData();
		playerData.setPlayerID(playerID);
		playerData.setPlayerName(playerName);
		
		this.playersStats.put(playerID, playerData);
	}
	
	public void removePlayer(PlayerData player) {
		this.playersStats.remove(player.getPlayerID());
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public PlayerData getPlayerStatsForID(int id) {
		return this.playersStats.get(id);
	}
	
	public final void setPlayers(PlayerData[] players) {
		if (players != null) {
			for (PlayerData player : players) {
				if (this.playersStats.containsKey(player.getPlayerID())) {
					this.playersStats.get(player.getPlayerID()).updateFrom(player);
				}
			}
		}
	}
}
