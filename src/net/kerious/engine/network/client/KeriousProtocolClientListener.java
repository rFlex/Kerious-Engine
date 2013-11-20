/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolClientListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 6:03:53 PM
////////

package net.kerious.engine.network.client;

public interface KeriousProtocolClientListener {

	boolean shouldAcceptConnection(KeriousProtocolClient keriousClient, String ip, int port);
	void onConnected(KeriousProtocolClient keriousClient, String ip, int port);
	void onConnectionFailed(KeriousProtocolClient keriousClient, String ip, int port, Exception thrownException);
	
}
