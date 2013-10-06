/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// DiscoverPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 28, 2012 at 12:19:05 AM
////////

package com.kerious.framework.network.protocol.packets;

import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

public class DiscoverPacket extends KeriousUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x13;
	private boolean discover;
	private String mapName;
	private int serverPort;
	private int connectedPlayer;
	private int maxPlayer;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new DiscoverPacket();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DiscoverPacket() {
		super(DiscoverPacket.byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void reset() {
		super.reset();
		
		this.discover = false;
		this.mapName = "";
		this.serverPort = 0;
		this.connectedPlayer = 0;
		this.maxPlayer = 0;
	}
	
	@Override
	protected void childUnpack() {
		this.discover = read(discover);
		this.mapName = read(mapName);
		
		this.serverPort = read(serverPort);
		this.connectedPlayer = read(connectedPlayer);
		this.maxPlayer = read(maxPlayer);
	}

	@Override
	protected void childPack() {
		write(discover);
		write(mapName);
		write(serverPort);
		write(connectedPlayer);
		write(maxPlayer);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public int size() {
		return super.size() + sizeof(discover) + sizeof(mapName) + sizeof(serverPort) + sizeof(connectedPlayer) + sizeof(maxPlayer);
	}

	final public boolean isDiscover() {
		return discover;
	}

	final public void setDiscover(boolean discover) {
		this.discover = discover;
	}

	final public String getMapName() {
		return this.mapName;
	}

	final public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	
	final public int getServerPort() {
		return serverPort;
	}

	final public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	final public int getConnectedPlayer() {
		return connectedPlayer;
	}

	final public void setConnectedPlayer(int connectedPlayer) {
		this.connectedPlayer = connectedPlayer;
	}

	final public int getMaxPlayer() {
		return maxPlayer;
	}

	final public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}
}
