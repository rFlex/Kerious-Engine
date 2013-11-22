/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolClientListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 6:03:53 PM
////////

package net.kerious.engine.network.client;

import com.badlogic.gdx.utils.ObjectMap;

public interface ClientServiceListener {

	void onReceivedWorldInformations(ClientService clientService, ObjectMap<String, String> informations, boolean shouldLoadWorld);
	void onDisconnected(ClientService clientService, String ip, int port, String reason);
	void onConnected(ClientService clientService, String ip, int port, int playerId);
	void onConnectionFailed(ClientService clientService, String ip, int port, String reason);
	
}
