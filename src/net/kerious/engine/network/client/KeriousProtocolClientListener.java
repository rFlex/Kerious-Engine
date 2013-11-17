/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolClientListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 6:03:53 PM
////////

package net.kerious.engine.network.client;

import java.net.InetSocketAddress;

public interface KeriousProtocolClientListener {

	void onConnectionAsked(KeriousProtocolClient keriousClient, String name, InetSocketAddress ip, int port);
	
}
