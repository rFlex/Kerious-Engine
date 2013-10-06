/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.network
// Packet.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2012 at 6:25:14 PM
////////

package com.kerious.framework.network;

import com.kerious.framework.network.protocol.KeriousUDPPacket;

public class Packet {

	////////////////////////
	// VARIABLES
	////////////////

	public final NetworkPeer sender;
	public final KeriousUDPPacket data;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Packet(NetworkPeer sender, KeriousUDPPacket packet) {
		this.sender = sender;
		this.data = packet;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
