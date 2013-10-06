/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.client.network
// ConnectionCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2012 at 8:18:45 PM
////////

package com.kerious.framework.client;

import java.net.InetSocketAddress;

import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.network.NetworkGate;
import com.kerious.framework.network.NetworkManager;
import com.kerious.framework.network.Packet;
import com.kerious.framework.network.protocol.packets.ConnectionPacket;
import com.kerious.framework.network.protocol.packets.KeriousPacket;
import com.kerious.framework.utils.IEventListener;

public class ConnectionCreator implements IEventListener<Packet>, Disposable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private IConnectionCreatorListener listener;
	private InetSocketAddress address;
	private NetworkManager nm;
	private NetworkGate gate;
	private DisconnectListener disconnectListener;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static interface IConnectionCreatorListener {
		void onConnectionDone(ConnectionCreator connectionCreator, ConnectionToken token);
		void onConnectionFailed(ConnectionCreator connectionCreator, String reason);
	}
	
	private class DisconnectListener implements IEventListener<NetworkGate> {
		@Override
		public void onFired(Object sender, NetworkGate arg) {
			ConnectionCreator.this.gate = null;
		}
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ConnectionCreator(IConnectionCreatorListener listener, NetworkManager nm, String ip, int port) {
		this.listener = listener;
		this.nm = nm;
		this.address = new InetSocketAddress(ip, port);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void negociateConnection() {
		this.dispose();
		
		this.gate = this.nm.openGate();

		if (this.gate != null) {
			this.disconnectListener = new DisconnectListener();
			
			gate.onPacketArrived.addListener(this);
			gate.onDisconnected.addListener(this.disconnectListener);
			gate.send(KeriousPacket.Connection.askConnect(), this.address);
		}
	}
	
	public void unlinkFromGate() {
		if (this.gate != null) {
			this.gate.onPacketArrived.removeListener(this);
			this.gate.onDisconnected.removeListener(this.disconnectListener);
			this.gate = null;
		}
	}
	
	@Override
	public void dispose() {
		if (this.gate != null) {
			this.gate.dispose();
			this.gate = null;
		}
	}
	
	@Override
	public void onFired(Object sender, Packet arg) {
		if (arg.data.packetType == ConnectionPacket.byteIdentifier) {
			this.unlinkFromGate();
			ConnectionPacket packet = (ConnectionPacket)arg.data;
			
			switch (packet.getConnectionRequest()) {
			case ConnectionPacket.CONNECTION_ACCEPTED:
				this.listener.onConnectionDone(this, new ConnectionToken(packet, arg.sender));
				break;
			case ConnectionPacket.CONNECTION_REFUSED_BANNED:
				this.listener.onConnectionFailed(this, "You are banned from this server.");
				break;
			case ConnectionPacket.CONNECTION_REFUSED_FULL:
				this.listener.onConnectionFailed(this, "The server is full.");
				break;
			}
		}
	}

	public static ConnectionCreator initiateConnection(IConnectionCreatorListener listener, NetworkManager nm, String destIP, int destPort) {
		ConnectionCreator creator = new ConnectionCreator(listener, nm, destIP, destPort);
		creator.negociateConnection();
		
		return creator;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public NetworkGate getGate() {
		return this.gate;
	}
}
