/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network
// PeerManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 27, 2012 at 10:35:07 PM
////////

package com.kerious.framework.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;

public class ReliableConnectionManager {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<Short, ReliableConnection> peers;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ReliableConnectionManager() {
		this.peers = new HashMap<Short, ReliableConnection>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void clear() {
		this.peers.clear();
	}
	
	public ReliableConnection getFromIdent(short ident, short code) {
		ReliableConnection peer = peers.get(ident);
		
		if (peer != null) {
			// Try to know if the client tried to connect with a bad ident/password combinaison
			if (peer.getCode() != code) {
				peer = null;
			}
		}
		
		return peer;
	}
	
	public void removeFromIdent(short ident) {
		peers.remove(ident);
	}
	
	public void remove(ReliableConnection peer) {
		peers.remove(peer.getIdentifier());
	}

	public ReliableConnection createConnection(short code, NetworkPeer peer) {
		ReliableConnection authPeer = null;
		
		// Who knows... Maybe it can happen
		if (peers.size() < Short.MAX_VALUE - 1) {
			while (authPeer == null) {
				final short ident = (short)MathUtils.random(Short.MAX_VALUE);
				if (peers.get(ident) == null) {
					authPeer = new ReliableConnection(ident, code, peer);
					peers.put(ident, authPeer);
				}
			}
		}
		
		return authPeer;
	}
	
	public ReliableConnection createEmptyConnection() {
		ReliableConnection peer = null;
		
		// Who knows... Maybe it can happen
		if (peers.size() < Short.MAX_VALUE - 1) {
			while (peer == null) {
				final short ident = (short)MathUtils.random(Short.MAX_VALUE);
				if (peers.get(ident) == null) {
					peer = new ReliableConnection(ident);
					peers.put(ident, peer);
				}
			}
		}
		
		return peer;
	}
	
	public ReliableConnection getFromPacket(NetworkPeer peer, KeriousReliableUDPPacket packet) {
		ReliableConnection authPeer = this.getFromIdent(packet.getIdent(), packet.getCode());
			
		if (authPeer != null) {
			authPeer.setNetworkpeer(peer);
		}
		
		return authPeer;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Collection<ReliableConnection> getPeers() {
		return this.peers.values();
	}
}
