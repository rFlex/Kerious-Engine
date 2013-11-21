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

import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.network.protocol.packet.WorldInformationsPacket;

public class ClientPeer extends KeriousProtocolPeer {

	////////////////////////
	// VARIABLES
	////////////////
	
	private int playerId;
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
		super.handlePacketReceived(packet);

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
}
