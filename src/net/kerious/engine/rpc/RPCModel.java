/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.rpc
// RPCCall.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 4:23:28 PM
////////

package net.kerious.engine.rpc;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class RPCModel extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int id;
	public int destinationId;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RPCModel() {

	}

	////////////////////////
	// METHODS
	////////////////

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
