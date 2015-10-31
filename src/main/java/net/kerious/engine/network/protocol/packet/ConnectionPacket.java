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

public class ConnectionPacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final static byte ConnectionAsk = 1;
	public final static byte ConnectionInterrupted = 2;
	public final static byte ConnectionAccepted = 3;
	
	public byte connectionRequest;
	public String reason;
	public String playerName;
	public int playerId;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ConnectionPacket() {
		super(TypeConnection);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.connectionRequest = buffer.get();
		this.reason = this.getString(buffer);
		this.playerName = this.getString(buffer);
		this.playerId = buffer.getInt();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.put(this.connectionRequest);
		this.putString(buffer, this.reason);
		this.putString(buffer, this.playerName);
		buffer.putInt(this.playerId);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.connectionRequest = 0;
		this.reason = null;
		this.playerName = null;
		this.playerId = 0;
		this.options |= OptionResendIfLost;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
