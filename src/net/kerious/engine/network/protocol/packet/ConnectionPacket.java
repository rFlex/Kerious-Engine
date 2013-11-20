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

public class ConnectionPacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final static byte CONNECTION_ASK = 1;
	public final static byte CONNECTION_RESP_REFUSED = 2;
	public final static byte CONNECTION_RESP_ACCEPTED = 3;
	
	public byte connectionRequest;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ConnectionPacket() {
		super(CONNECTION_TYPE);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer)
			throws IOException {
		this.connectionRequest = buffer.get();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.put(this.connectionRequest);
	}

	@Override
	public void copyTo(KeriousSerializableData object) {
		ConnectionPacket packet = (ConnectionPacket)object;
		packet.connectionRequest = this.connectionRequest;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
