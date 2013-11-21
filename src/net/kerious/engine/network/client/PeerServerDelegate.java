/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolPeerServerDelegate.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:57:32 PM
////////

package net.kerious.engine.network.client;

import com.badlogic.gdx.utils.ObjectMap;

public interface PeerServerDelegate {
	
	void fillWorldInformations(ClientPeer peer, ObjectMap<String, String> informations);

}
