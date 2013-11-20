/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousReliablePacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 16, 2013 at 11:49:25 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class KeriousReliablePacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int sequence;
	public int lastSequenceReceived;
	public int ack;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousReliablePacket(byte packetType) {
		super(packetType);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) {
		this.sequence = buffer.getInt();
		this.lastSequenceReceived = buffer.getInt();
		this.ack = buffer.getInt();
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putInt(this.sequence);
		buffer.putInt(this.lastSequenceReceived);
		buffer.putInt(this.ack);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.sequence = 0;
		this.lastSequenceReceived = 0;
		this.ack = 0;
	}

	@Override
	public void copyTo(KeriousSerializableData object) {
		KeriousReliablePacket keriousReliablePacket = (KeriousReliablePacket)object;
		
		keriousReliablePacket.sequence = this.sequence;
		keriousReliablePacket.lastSequenceReceived = this.lastSequenceReceived;
		keriousReliablePacket.ack = this.ack;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
