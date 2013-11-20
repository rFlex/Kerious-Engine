/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousProtocolPeerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 19, 2013 at 2:11:44 AM
////////

package net.kerious.engine.network.protocol;

public interface KeriousProtocolPeerListener {

	void onConnectionFailed(KeriousProtocolPeer peer, Exception exception);
	void onConnected(KeriousProtocolPeer peer);
	void onDisconnected(KeriousProtocolPeer peer, String reason);
	
}
