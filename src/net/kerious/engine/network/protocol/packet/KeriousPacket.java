/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 16, 2013 at 11:46:34 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

@SuppressWarnings("rawtypes")
public class KeriousPacket extends KeriousSerializableData<KeriousPacket> {

	////////////////////////
	// VARIABLES
	////////////////

	public byte packetType;
	public KeriousSerializableData childPacket;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousPacket() {
		
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		this.packetType = buffer.get();
		
		this.childPacket = (KeriousSerializableData)protocol.createPacket(this.packetType);
		this.childPacket.deserialize(protocol, buffer);
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.put(this.packetType);
		
		if (this.childPacket != null) {
			this.childPacket.serialize(protocol, buffer);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.packetType = 0;
		if (this.childPacket != null) {
			this.childPacket.release();
			this.childPacket = null;
		}
	}

	@Override
	public void copyTo(KeriousPacket object) {
		object.packetType = this.packetType;
		
		if (this.childPacket != null) {
			object.childPacket = this.childPacket.clone();
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
