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

public abstract class KeriousPacket extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte TypeInformation = 1;
	public static final byte TypeConnection = 2;
	public static final byte TypePlayerCommand = 3;
	public static final byte TypeKeepAlive = 4;
	public static final byte TypeSnapshot = 5;
	public static final byte TypeRequest = 6;
	public static final byte TypeWorldInformations = 7;
	public static final byte TypeBasicCommand = 8;
	
	public static final byte OptionIgnoreIfLost = 0x0;
	public static final byte OptionResendIfLost = 0x1;

	final public byte packetType;
	public int sequence;
	public int lastSequenceReceived;
	public int ack;
	public byte options;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousPacket(byte packetType) {
		this.packetType = packetType;
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
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
		this.options = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
