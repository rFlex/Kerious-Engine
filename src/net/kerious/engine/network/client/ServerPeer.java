/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolPeerClient.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:30:05 PM
////////

package net.kerious.engine.network.client;

import java.net.InetAddress;

import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.ServerPeerListener;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.InformationPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.network.protocol.packet.WorldInformationsPacket;

/**
 * Portrays a connection with a remote server
 * @author simoncorsin
 *
 */
public class ServerPeer extends KeriousProtocolPeer {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final int MAX_CONNECTION_ATTEMPTS = 3;
	public static final float CONNECTION_ATTEMPT_WAITING_TIME = 1f;
	
	private boolean connected;
	private boolean shouldLoadWorld;
	private int connectionAttempt;
	private float nextConnectionAttempt;
	private ServerPeerListener listener;
	private String name;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ServerPeer(InetAddress address, int port) {
		super(address, port);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (!this.connected) {
			nextConnectionAttempt -= deltaTime;
			if (nextConnectionAttempt <= 0) {
				if (connectionAttempt == MAX_CONNECTION_ATTEMPTS) {
					if (this.listener != null) {
						this.listener.onConnectionFailed(this, "Connection failed after " + connectionAttempt + " retries");
					}
					this.setExpired(true);
				} else {
					this.sendConnectionPacket();
					nextConnectionAttempt = CONNECTION_ATTEMPT_WAITING_TIME;
					connectionAttempt++;
				}
			}
		}
	}
	
	private void sendConnectionPacket() {
		ConnectionPacket packet = this.protocol.createConnectionPacket(ConnectionPacket.ConnectionAsk);
		packet.playerName = this.name;
		
		this.send(packet);
		
		packet.release();
	}
	
	@Override
	public boolean handlePacketReceived(KeriousPacket packet) {
		if (super.handlePacketReceived(packet)) {
			return true;
		}
		
//		System.out.println("Received from server: " + packet.getClass().getSimpleName());
		
		switch (packet.packetType) {
		case KeriousPacket.TypeConnection:
			ConnectionPacket connectionPacket = (ConnectionPacket)packet;
			switch (connectionPacket.connectionRequest) {
			case ConnectionPacket.ConnectionAccepted:
				if (!this.connected) {
					this.connected = true;
					if (this.listener != null) {
						this.listener.onConnected(this, connectionPacket.playerId);
					}
				}
				break;
			case ConnectionPacket.ConnectionInterrupted:
				if (this.listener != null) {
					if (this.connected) {
						this.listener.onDisconnected(this, connectionPacket.reason);
					} else {
						this.listener.onConnectionFailed(this, connectionPacket.reason);
					}
				}
				this.connected = false;
				break;
			default:
				return false;
			}
			break;
		case KeriousPacket.TypeRequest:
			RequestPacket requestPacket = (RequestPacket)packet;
			switch (requestPacket.request) {
			case RequestPacket.RequestLoadWorld:
				if (!this.shouldLoadWorld) {
					this.shouldLoadWorld = true;
					RequestPacket requestInfo = this.protocol.createRequestPacket(RequestPacket.RequestReceiveWorldInformations);
					this.send(requestInfo);
				}
				break;
			}
			break;
		case KeriousPacket.TypeWorldInformations:
			WorldInformationsPacket worldInformationsPacket = (WorldInformationsPacket)packet;
			if (this.listener != null) {
				this.listener.onReceivedWorldInformations(worldInformationsPacket.informations, this.shouldLoadWorld);
			}
			
			this.shouldLoadWorld = false;
			break;
		case KeriousPacket.TypeInformation:
			InformationPacket informationPacket = (InformationPacket)packet;
			
			switch (informationPacket.information) {
			case InformationPacket.InformationServerIsLoading:
				if (this.listener != null) {
					this.listener.onRemoteIsLoadingWorld();
				}
				break;
			case InformationPacket.InformationServerFailedLoading:
				if (this.listener != null) {
					this.listener.onRemoteFailedToLoadWorld(informationPacket.informationString);
				}
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
	
	public boolean isConnected() {
		return connected;
	}

	public ServerPeerListener getListener() {
		return listener;
	}

	public void setListener(ServerPeerListener listener) {
		this.listener = listener;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
