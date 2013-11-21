/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol.packet
// RequestPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 2:19:31 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class RequestPacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final byte RequestReceiveWorldInformations = 1;
	public static final byte RequestBeginReceiveSnapshots = 2;
	public static final byte RequestEndReceiveSnapshots = 3;
	
	public byte request;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public RequestPacket() {
		super(TypeRequest);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void reset() {
		super.reset();
		
		this.request = 0;
		this.options |= OptionResendIfLost;
	}
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.request = buffer.get();
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.put(this.request);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
