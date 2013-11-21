/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolClientListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 6:03:53 PM
////////

package net.kerious.engine.network.client;

import net.kerious.engine.network.protocol.packet.WorldInformationsPacket;
import net.kerious.engine.world.World;

public interface KeriousProtocolClientListener {

	World createWorld(WorldInformationsPacket worldInformations);
	
	void onDisconnected(KeriousProtocolAbstract keriousClient, String ip, int port, String reason);
	void onConnected(KeriousProtocolAbstract keriousClient, String ip, int port);
	void onConnectionFailed(KeriousProtocolAbstract keriousClient, String ip, int port, String reason);
	
}
