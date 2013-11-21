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

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.network.protocol.ServerPeerListener;
import net.kerious.engine.network.protocol.packet.KeriousPacket;

public class KeriousProtocolClient extends KeriousProtocolAbstract implements ServerPeerListener {

	////////////////////////
	// VARIABLES
	////////////////

	private KeriousProtocolClientListener listener;
	private ServerPeer serverPeer;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocolClient(KeriousEngine engine) throws SocketException {
		super(engine);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void disconnect() {
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
	
	public void connectTo(String ip, int port) {
		this.disconnect();
		
		try {
			ServerPeer peer = this.createPeer(InetAddress.getByName(ip), port);
			peer.setListener(this);
			
			this.serverPeer = peer;
		} catch (Exception e) {
			if (this.listener != null) {
				this.listener.onConnectionFailed(this, ip, port, e.getMessage());
			}
		}
	}
	
	@Override
	public void onConnected(ServerPeer peer) {
		if (this.listener != null) {
			this.listener.onConnected(this, peer.getIP(), peer.getPort());
		}
	}
	
	@Override
	public void onConnectionFailed(ServerPeer peer, String reason) {
		this.disconnect();
		if (this.listener != null) {
			this.listener.onConnectionFailed(this, peer.getIP(), peer.getPort(), reason);
		}
	}

	@Override
	public void onDisconnected(ServerPeer peer, String reason) {
		this.disconnect();
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

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KeriousProtocolClientListener getListener() {
		return listener;
	}

	public void setListener(KeriousProtocolClientListener listener) {
		this.listener = listener;
	}
}
