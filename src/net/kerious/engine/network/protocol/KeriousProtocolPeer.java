/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousPeer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 8:17:40 PM
////////

package net.kerious.engine.network.protocol;

import net.kerious.engine.network.peer.INetworkPeerListener;
import net.kerious.engine.network.peer.NetworkPeer;

public class KeriousProtocolPeer extends NetworkPeer implements INetworkPeerListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	private boolean connected;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocolPeer(String ip, int port) {
		super(ip, port);
	}
	
	public KeriousProtocolPeer(NetworkPeer networkPeer) {
		super(networkPeer);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void onReceived(NetworkPeer peer, Object packet) {
		
	}

	@Override
	public void onFailedReceived(NetworkPeer peer, Exception exception) {
		
	}

	@Override
	public void onSent(NetworkPeer peer, Object packet) {
		
	}

	@Override
	public void onFailedSend(NetworkPeer peer, Object packet, Exception exception) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

}
