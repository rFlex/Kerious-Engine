/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol.packets
// InfoPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2012 at 4:48:02 PM
////////

package com.kerious.framework.network.protocol.packets;

import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

public class InformationPacket extends KeriousUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x2;
	
	public static final byte VOID = 0x0;
	public static final byte FORBIDDEN = 0x1;
	public static final byte UNSUPPORTED = 0x2;
	public static final byte DISCONNECTED = 0x3;
	
	private byte information;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new InformationPacket();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public InformationPacket() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void reset() {
		super.reset();
		
		this.information = VOID;
	}
	
	@Override
	protected void childUnpack() {
		this.information = read(information);
	}

	@Override
	protected void childPack() {
		write(this.information);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final int size() {
		return super.size() + sizeof(this.information);
	}
	
	public final byte getInformation() {
		return information;
	}

	public final void setInformation(byte information) {
		this.information = information;
	}
	
}
