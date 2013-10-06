package com.kerious.framework.network.protocol.packets;

import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class KeepAlivePacket extends KeriousReliableUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x16;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new KeepAlivePacket();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeepAlivePacket() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void childUnpack() {
		super.childUnpack();
	}

	@Override
	protected void childPack() {
		super.childPack();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int size() {
		return super.size();
	}
}
