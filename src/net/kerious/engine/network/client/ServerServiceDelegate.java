/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolServerDelegate.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:24:20 PM
////////

package net.kerious.engine.network.client;

import me.corsin.javatools.misc.ValueHolder;

import com.badlogic.gdx.utils.ObjectMap;

public interface ServerServiceDelegate {
	
	void fillWorldInformations(ServerService server, ObjectMap<String, String> informations);
	boolean shouldAcceptConnection(ServerService server, String ip, int port, ValueHolder<String> outReason);
	void updateWorldWithCommands(ServerService server, int playerId, float directionAngle, float directionStrength, long actions);

}
