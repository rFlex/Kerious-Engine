/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolPeerServer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:54:08 PM
////////

package net.kerious.engine.network.client;

import java.net.InetAddress;

import net.kerious.engine.entity.Entity;
import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.network.protocol.packet.SnapshotPacket;
import net.kerious.engine.network.protocol.packet.WorldInformationsPacket;
import net.kerious.engine.player.Player;
import net.kerious.engine.world.World;

public class ClientPeer extends KeriousProtocolPeer {

	////////////////////////
	// VARIABLES
	////////////////
	
	private int playerId;
	private String name;
	private PeerServerDelegate delegate;
	private boolean readyToReceiveSnapshots;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ClientPeer(InetAddress address, int port) {
		super(address, port);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public boolean handlePacketReceived(KeriousPacket packet) {
		if (super.handlePacketReceived(packet)) {
			return true;
		}

		switch (packet.packetType) {
		case KeriousPacket.TypeConnection:
			ConnectionPacket connectionPacket = (ConnectionPacket)packet;
			
			switch (connectionPacket.connectionRequest) {
			case ConnectionPacket.ConnectionAsk:
				// Send the accepted packet
				this.send(this.protocol.createConnectionPacket(ConnectionPacket.ConnectionAccepted));
				break;
			case ConnectionPacket.ConnectionInterrupted:
				this.setDisconnectReason(connectionPacket.reason);
				this.setExpired(true);
			default:
				return false;
			}
			break;
		case KeriousPacket.TypeRequest:
			RequestPacket requestPacket = (RequestPacket)packet;
			
			switch (requestPacket.request) {
			case RequestPacket.RequestBeginReceiveSnapshots:
				this.readyToReceiveSnapshots = true;
				break;
			case RequestPacket.RequestEndReceiveSnapshots:
				this.readyToReceiveSnapshots = false;
				break;
			case RequestPacket.RequestReceiveWorldInformations:
				WorldInformationsPacket worldInformationsPacket = this.protocol.createWorldInformationsPacket();
				if (this.delegate != null) {
					this.delegate.fillWorldInformations(this, worldInformationsPacket.informations);
				}
				this.send(worldInformationsPacket);
				break;
			default:
				return false;
			}
			break;
		default:
			return false;
		}
		return true;
	}
	
	public void sendSnapshot(World world) {
//		this.sendKeepAlivePacket();
		
		SnapshotPacket snapshotPacket = this.protocol.createSnapshotPacket();
		
		for (Entity<?, ?> entity : world.getEntityManager().getEntites()) {
			snapshotPacket.addModel(entity.getModel());
		}

		for (Player player : world.getPlayerManager().getPlayers()) {
			snapshotPacket.addPlayer(player);
		}
		
		this.send(snapshotPacket);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public boolean isReadyToReceiveSnapshots() {
		return readyToReceiveSnapshots;
	}

	public void setReadyToReceiveSnapshots(boolean readyToReceiveSnapshots) {
		this.readyToReceiveSnapshots = readyToReceiveSnapshots;
	}

	public PeerServerDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(PeerServerDelegate delegate) {
		this.delegate = delegate;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
