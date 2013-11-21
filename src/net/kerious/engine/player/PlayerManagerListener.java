/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// PlayerManagerListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 6:38:21 PM
////////

package net.kerious.engine.player;

public interface PlayerManagerListener {
	
	void onPlayerConnected(Player player);
	void onPlayerDisconnected(Player player, String reason);

}
