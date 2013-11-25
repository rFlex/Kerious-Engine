/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// ClientGameListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 8:36:44 PM
////////

package net.kerious.engine.networkgame;

import net.kerious.engine.world.World;

public interface ClientGameListener {

	void onGameWorldLoaded(ClientGame game, World world);
	void onDisconnected(ClientGame game, String ip, int port, String reason);
	void onConnected(ClientGame game, String ip, int port);
	void onConnectionFailed(ClientGame game, String ip, int port, String reason);
	
}
