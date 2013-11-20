/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol.packet
// KeepAlivePacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 20, 2013 at 1:27:15 PM
////////

package net.kerious.engine.network.protocol.packet;

public class KeepAlivePacket extends KeriousReliablePacket {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeepAlivePacket() {
		super(KEEP_ALIVE_TYPE);
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
