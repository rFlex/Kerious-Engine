/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolClient.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:11:29 PM
////////

package net.kerious.engine.network.client;

import java.net.InetAddress;
import java.net.SocketException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.protocol.ServerPeerListener;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.player.Player;
import net.kerious.engine.world.event.Event;

public class ClientService extends AbstractKeriousProtocolService implements ServerPeerListener {

	////////////////////////
	// VARIABLES
	////////////////

	private ClientServiceListener listener;
	private ServerPeer serverPeer;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ClientService() throws SocketException {
		super();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (this.serverPeer != null) {
			if (this.serverPeer.hasExpired()) {
				this.onDisconnected(this.serverPeer, this.serverPeer.getDisconnectReason());
			} else {
				this.serverPeer.update(deltaTime);
				this.serverPeer.sendKeepAlivePacket();
			}
		}
	}
	
	public void disconnect(String reason) {
		if (this.serverPeer != null) {
			ConnectionPacket connection = this.protocol.createConnectionPacket(ConnectionPacket.ConnectionInterrupted);
			connection.reason = reason;
			
			this.serverPeer.send(connection);
			this.onDisconnected(this.serverPeer, reason);
		}
	}
	
	private void destroyPeer() {
		if (this.serverPeer != null) {
			this.serverPeer.setListener(null);
			this.serverPeer = null;
		}
	}
	
	/**
	 * Create a peer that represents a connection between this client and the server
	 * @param address
	 * @param port
	 * @return
	 */
	protected ServerPeer createPeer(InetAddress address, int port) {
		return new ServerPeer(address, port);
	}
	
	public void connectTo(String name, String ip, int port) {
		this.disconnect("Connected to another server");
		
		try {
			ServerPeer peer = this.createPeer(InetAddress.getByName(ip), port);
			peer.setProtocol(this.getProtocol());
			peer.setName(name);
			peer.setListener(this);
			peer.setGate(this.getGate());
			
			this.serverPeer = peer;
		} catch (Exception e) {
			if (this.listener != null) {
				this.listener.onConnectionFailed(this, ip, port, e.getMessage());
			}
		}
	}
	
	@Override
	public void onConnected(ServerPeer peer, int playerId) {
		if (this.listener != null) {
			this.listener.onConnected(this, peer.getIP(), peer.getPort(), playerId);
		}
	}
	
	@Override
	public void onConnectionFailed(ServerPeer peer, String reason) {
		this.destroyPeer();
		if (this.listener != null) {
			this.listener.onConnectionFailed(this, peer.getIP(), peer.getPort(), reason);
		}
	}

	@Override
	public void onDisconnected(ServerPeer peer, String reason) {
		this.destroyPeer();
		if (this.listener != null) {
			this.listener.onDisconnected(this, peer.getIP(), peer.getPort(), reason);
		}
	}
	
	@Override
	public void onReceived(InetAddress address, int port, Object packet) {
		KeriousPacket keriousPacket = (KeriousPacket)packet;
		
		if (this.serverPeer != null) {
			if (this.serverPeer.getAddress().equals(address) && this.serverPeer.getPort() == port) {
				this.serverPeer.handlePacketReceived(keriousPacket);
			}
		}
		
		keriousPacket.release();
	}
	
	public void sendToServer(KeriousPacket packet) {
		if (this.serverPeer == null) {
			throw new KeriousException("The client is currently not connected to any server");
		}
		
		this.serverPeer.send(packet);
	}
	
	@Override
	public void onReceivedWorldInformations(ServerPeer peer, ObjectMap<String, String> informations, boolean shouldLoadWorld) {
		if (this.listener != null) {
			this.listener.onReceivedWorldInformations(this, informations, shouldLoadWorld);
		}
	}
	
	@Override
	public void onReceivedInformation(ServerPeer peer, String informationType, String information) {
		if (this.listener != null) {
			this.listener.onReceivedInformation(this, informationType, information);
		}
	}

	@Override
	public void onReceivedSnapshot(ServerPeer peer, Array<Player> players, Array<EntityModel> entityModels, Array<Event> events) {
		if (this.listener != null) {
			this.listener.onReceivedSnapshot(this, players, entityModels, events);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public ClientServiceListener getListener() {
		return listener;
	}

	public void setListener(ClientServiceListener listener) {
		this.listener = listener;
	}
	
	public ServerPeer getServerPeer() {
		return this.serverPeer;
	}
	
	public boolean isConnected() {
		return this.serverPeer != null;
	}
}
