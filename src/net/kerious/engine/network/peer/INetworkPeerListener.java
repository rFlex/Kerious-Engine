/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.peer
// INetworkPeerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 2:15:43 PM
////////

package net.kerious.engine.network.peer;

public interface INetworkPeerListener {

	void onReceived(NetworkPeer peer, Object packet);
	void onFailedReceived(NetworkPeer peer, Exception exception);
	void onSent(NetworkPeer peer, Object packet);
	void onFailedSend(NetworkPeer peer, Object packet, Exception exception);
	
}
