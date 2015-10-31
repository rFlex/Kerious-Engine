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
import net.kerious.engine.network.protocol.packet.SnapshotPacket;

import com.badlogic.gdx.utils.ObjectMap;

public interface ServerPeerListener {

	void onReceivedSnapshot(ServerPeer peer, SnapshotPacket snapshotPacket);
	void onReceivedWorldInformations(ServerPeer peer, ObjectMap<String, String> informations, boolean shouldLoadWorld);
	void onReceivedInformation(ServerPeer peer, String informationType, String information);
	void onConnected(ServerPeer peer, int playerId);
	void onConnectionFailed(ServerPeer peer, String reason);
	void onDisconnected(ServerPeer peer, String reason);
	
}
