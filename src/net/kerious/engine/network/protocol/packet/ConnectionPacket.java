/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol.packet
// ConnectionPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 5:57:36 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class ConnectionPacket extends KeriousSerializableData<ConnectionPacket> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void copyTo(ConnectionPacket packet) {
		
	}

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer)
			throws IOException {
		
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
