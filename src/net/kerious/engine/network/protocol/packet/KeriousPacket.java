/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 16, 2013 at 11:46:34 PM
////////

package net.kerious.engine.network.protocol.packet;

import net.kerious.engine.network.protocol.KeriousSerializableData;

public abstract class KeriousPacket extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte INFORMATION_TYPE = 1;
	public static final byte CONNECTION_TYPE = 2;
	public static final byte PLAYER_COMMAND_TYPE = 3;
	public static final byte KEEP_ALIVE_TYPE = 4;
	public static final byte SNAPSHOT_TYPE = 10;

	final public byte packetType;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousPacket(byte packetType) {
		this.packetType = packetType;
	}

	////////////////////////
	// METHODS
	////////////////
	
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
