/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol.packet
// InformationPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 22, 2013 at 5:36:48 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class InformationPacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final static byte InformationServerIsLoading = 1;
	public final static byte InformationServerFailedLoading = 2;
	
	public byte information;
	public String informationString;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public InformationPacket() {
		super(TypeInformation);
	}

	////////////////////////
	// METHODS
	////////////////

	
	@Override
	public void reset() {
		super.reset();
		
		this.information = 0;
		this.informationString = null;
		this.options |= OptionResendIfLost;
	}
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.information = buffer.get();
		this.informationString = this.getString(buffer);
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.put(this.information);
		this.putString(buffer, this.informationString);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
