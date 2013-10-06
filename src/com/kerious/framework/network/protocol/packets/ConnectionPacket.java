/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// ConnectionPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 4:38:10 PM
////////

package com.kerious.framework.network.protocol.packets;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class ConnectionPacket extends KeriousUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x10;
	
	public static final byte VOID = 0x0;
	public static final byte CONNECTION_ASK = 0x1;
	public static final byte DISCONNECTION_ASK = 0x2;
	public static final byte CONNECTION_ACCEPTED = 0x3;
	public static final byte CONNECTION_REFUSED_FULL = 0x4;
	public static final byte CONNECTION_REFUSED_BANNED = 0x5;
	public static final byte DISCONNECTION_ACCEPTED = 0x6;
	
	private short channel;
	private short code;
	private int playerID;
	private byte connectionRequest;
	private byte playerSkin;
	private String playerName;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new ConnectionPacket();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ConnectionPacket() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void reset() {
		super.reset();
		
		this.channel = 0;
		this.code = 0;
		this.playerID = 0;
		this.connectionRequest = ConnectionPacket.VOID;
		this.playerSkin = 0;
		this.playerName = "";
	}
	
	@Override
	protected void childUnpack() {
		this.channel = read(channel);
		this.code = read(code);
		this.playerID = read(playerID);
		this.connectionRequest = read(connectionRequest);
		this.playerSkin = read(playerSkin);
		this.playerName = read(playerName);
	}

	@Override
	protected void childPack() {
		write(this.channel);
		write(this.code);
		write(this.playerID);
		write(this.connectionRequest);
		write(this.playerSkin);
		write(this.playerName);
	}

	public String stringResponse() {
		switch (this.connectionRequest) {
		case CONNECTION_REFUSED_FULL:
			return "Server is full";
		case CONNECTION_REFUSED_BANNED:
			return "You are banned from this server";
		default:
			return "Unsupported connection request.";
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public int size() {
		return super.size() + sizeof(this.channel) + sizeof(this.code) + sizeof(this.playerID) + sizeof(this.connectionRequest) + sizeof(this.playerSkin) + sizeof(this.playerName);
	}
	
	public final byte getConnectionRequest() {
		return connectionRequest;
	}

	public final void setConnectionRequest(byte connectionRequest) {
		this.connectionRequest = connectionRequest;
	}

	public final short getChannel() {
		return channel;
	}

	public final void setIdentifier(short channel) {
		this.channel = channel;
	}

	public final short getCode() {
		return code;
	}

	public final void setCode(short code) {
		this.code = code;
	}

	final public int getPlayerID() {
		return playerID;
	}

	final public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	final public byte getPlayerSkin() {
		return playerSkin;
	}

	final public void setPlayerSkin(byte playerSkin) {
		this.playerSkin = playerSkin;
	}

	public final String getPlayerName() {
		return playerName;
	}

	public final void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

}
