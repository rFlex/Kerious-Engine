/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// Player.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 6:20:19 PM
////////

package net.kerious.engine.player;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class Player extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int id;
	public String name;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void copyTo(KeriousSerializableData object) {
		Player player = (Player)object;
		
		player.id = this.id;
		player.name = this.name;
	}

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		this.id = buffer.getInt();
		this.name = this.getString(buffer);
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putInt(this.id);
		this.putString(buffer, this.name);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.id = 0;
		this.name = null;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
