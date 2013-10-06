/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.world.entities
// BaseUser.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 9, 2012 at 5:06:38 PM
////////

package com.kerious.framework.world.entities;

import com.kerious.framework.KeriousObjectFactory;
import com.kerious.framework.network.Compressable;
import com.kerious.framework.network.Packable;
import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.server.User;

public class PlayerData implements Packable, Compressable<PlayerData> {

	////////////////////////
	// VARIABLES
	////////////////

	protected byte maskBitField;
	
	private static final int PLAYERNAME = 0;
	protected static final int FIRST_FREE_POSITION = 1; 
	
	private String playerName;
	private int playerID;
	private User user;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PlayerData() {
		this.playerName = "";
		this.maskBitField = -1;
	}
	
	////////////////////////
	// METHODS
	////////////////

	public PlayerData snapshot() {
		PlayerData playerInformations = KeriousObjectFactory.createPlayerData();
		
		playerInformations.setPlayerID(this.playerID);
		playerInformations.setPlayerName(this.playerName);
		
		return playerInformations;
	}
	
	public void updateFrom(PlayerData playerData) {
		this.playerID = playerData.playerID;
		this.playerName = playerData.playerName;
	}
	
	@Override
	public void pack(ReaderWriter rw) {
		rw.write(this.playerID);
		rw.write(this.maskBitField);
		
		if ((this.maskBitField & (0x1 << PLAYERNAME)) >> PLAYERNAME == 0x1) {
			rw.write(this.playerName);
		}
	}

	@Override
	public void unpack(ReaderWriter rw) {
		this.playerID = rw.read(playerID);
		this.maskBitField = rw.read(maskBitField);
		
		if ((this.maskBitField & (0x1 << PLAYERNAME)) >> PLAYERNAME == 0x1) {
			this.playerName = rw.read(playerName);
		}
	}
	
	@Override
	public void compress(PlayerData delta) {
		if (this.playerName.equals(delta.playerName)) {
			byte mask = this.maskBitField;
			this.maskBitField = (mask &= (-1) ^ (0x1 << PLAYERNAME));
		}
	}

	@Override
	public void decompress(PlayerData delta) {
		if (!((this.maskBitField & (0x1 << PLAYERNAME)) >> PLAYERNAME == 0x1)) {
			this.playerName = delta.playerName;
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final String getPlayerName() {
		return playerName;
	}
	
	public final void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public final void setUser(User user) {
		this.user = user;
	}
	
	public final User getUser() {
		return this.user;
	}

	public final int getPlayerID() {
		return playerID;
	}
	
	public final void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
}
