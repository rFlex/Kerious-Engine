/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// PacketHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 25, 2012 at 7:16:47 PM
////////

package com.kerious.framework.server;

import com.kerious.framework.network.NetworkPeer;
import com.kerious.framework.network.Packet;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.network.protocol.packets.ConnectionPacket;
import com.kerious.framework.network.protocol.packets.KeriousPacket;
import com.kerious.framework.utils.IEventListener;

public class PacketHandler implements IEventListener<Packet> {

	////////////////////////
	// VARIABLES
	////////////////

	private KeriousServer server;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PacketHandler(KeriousServer server) {
		this.server = server;
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void onFired(Object sender, Packet arg) {
		KeriousUDPPacket packet = arg.data;
		
		if (packet.reliable) {
			this.handleReliable(arg.sender, (KeriousReliableUDPPacket)packet);
		} else {
			switch (arg.data.packetType) {
			case ConnectionPacket.byteIdentifier:
				this.handleConnection(arg.sender, (ConnectionPacket)packet);
				break;
			default:
				this.server.onUnmanagedPacketReceived.call(arg.sender, packet);
				break;
			}
		}
	}
	
	private void handleReliable(NetworkPeer peer, KeriousReliableUDPPacket packet) {
		final User user = this.server.userManager.getUserFromIdent(packet.getIdent(), packet.getCode());
		
		if (user != null) {
			user.handlePacket(peer, packet);
		} else {
			peer.send(KeriousPacket.Information.forbidden());
		}
	}
	
	private void handleConnection(NetworkPeer peer, ConnectionPacket packet) {
		switch (packet.getConnectionRequest()) {
		case ConnectionPacket.CONNECTION_ASK:
			if (this.server.intendent.shouldAcceptConnection(packet, peer)) {
				final User user = this.server.userManager.addUser(packet.getPlayerName(), packet.getCode(), peer);
				
				if (user == null) {
					peer.send(KeriousPacket.Connection.refuseConnectDueToFullServer());
				}
			} else {
				peer.send(KeriousPacket.Connection.refuseConnectDueToFullServer());
			}
			break;
		case ConnectionPacket.DISCONNECTION_ASK:
			final User user = this.server.userManager.getUserFromIdent(packet.getChannel(), packet.getCode());
			
			if (user != null) {
				this.server.userManager.removeUser(user);
			} else {
				peer.send(KeriousPacket.Information.forbidden());
			}
			
			break;
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
