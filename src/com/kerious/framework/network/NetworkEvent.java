/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.network
// Packet.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 22, 2012 at 8:44:37 PM
////////

package com.kerious.framework.network;

import com.kerious.framework.network.protocol.KeriousUDPPacket;

public class NetworkEvent {

	////////////////////////
	// VARIABLES
	////////////////

	public final EventType event;
	public final Packet packet;
	
	////////////////////////
	// NESTED CLASSES
	////////////////

	public enum EventType {
		SEND_DATA,
		DATA_RECEIVED,
		DISCONNECTED
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public NetworkEvent(EventType event, KeriousUDPPacket data, NetworkPeer sender) {
		this.event = event;
		this.packet = new Packet(sender, data);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	public static NetworkEvent sendData(KeriousUDPPacket data, NetworkPeer dest) {
		return new NetworkEvent(EventType.SEND_DATA, data, dest);
	}
	
	public static NetworkEvent dataReceived(KeriousUDPPacket data, NetworkPeer sender) {
		return new NetworkEvent(EventType.DATA_RECEIVED, data, sender);
	}
	
	public static NetworkEvent disconnected() {
		return new NetworkEvent(EventType.DISCONNECTED, null, null);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
