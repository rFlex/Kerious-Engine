/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolServerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 11:21:31 PM
////////

package net.kerious.engine.network.client;

public interface ServerServiceListener {

	void onPeerConnected(ServerService server, ClientPeer client);
	void onPeerDisconnected(ServerService server, ClientPeer client);
	void onPeerBecameReadyToReceiveSnapshots(ServerService server, ClientPeer client);

}
