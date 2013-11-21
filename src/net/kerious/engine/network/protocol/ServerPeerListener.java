/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousProtocolPeerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 19, 2013 at 2:11:44 AM
////////

package net.kerious.engine.network.protocol;

import net.kerious.engine.network.client.ServerPeer;

public interface ServerPeerListener {

	void onConnected(ServerPeer peer);
	void onConnectionFailed(ServerPeer peer, String reason);
	void onDisconnected(ServerPeer peer, String reason);
	
}
