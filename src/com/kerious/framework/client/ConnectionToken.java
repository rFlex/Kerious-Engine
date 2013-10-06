/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.client.pages
// ConnectionToken.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2012 at 1:45:07 PM
////////

package com.kerious.framework.client;

import com.kerious.framework.network.ReliableConnection;
import com.kerious.framework.network.NetworkGate;
import com.kerious.framework.network.NetworkPeer;
import com.kerious.framework.network.protocol.packets.ConnectionPacket;

public class ConnectionToken {

	////////////////////////
	// VARIABLES
	////////////////

	public final ReliableConnection serverPeer;
	public final NetworkGate gate;
	public final int playerID;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ConnectionToken(ConnectionPacket connectionPacket, NetworkPeer sender) {
		this.serverPeer = new ReliableConnection(connectionPacket.getChannel(), connectionPacket.getCode(), sender);
		this.gate = sender.getGate();
		this.playerID = connectionPacket.getPlayerID();
	}
	
	public ConnectionToken(ReliableConnection serverPeer, NetworkGate gate, int playerID) {
		this.serverPeer = serverPeer;
		this.gate = gate;
		this.playerID = playerID;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
