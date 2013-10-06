package com.kerious.framework.network;

import java.net.InetSocketAddress;

import com.kerious.framework.network.protocol.KeriousUDPPacket;

public class NetworkPeer {

	////////////////////////
	// VARIABLES
	////////////////
	
	protected InetSocketAddress address;
	protected NetworkGate gate;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetworkPeer() {
		
	}

	////////////////////////
	// METHODS
	////////////////

	public void send(KeriousUDPPacket packet) {
		if (this.gate == null) {
			throw new RuntimeException("The peer has not any gate setted.");
		}
		
		this.gate.send(packet, this.address);
	}
	
	@Override
	public String toString() {
		return this.getAddress();
	}
	
	public static NetworkPeer create(InetSocketAddress remoteAddr) {
		NetworkPeer peer = new NetworkPeer();
		
		peer.address = remoteAddr;
		
		return peer;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	final public String getIP() {
		return this.address.getAddress().getHostAddress();
	}
	
	final public int getPort() {
		return this.address.getPort();
	}
	
	final public InetSocketAddress getSocketAddress() {
		return this.address;
	}
	
	final public String getAddress() {
		return this.getIP() + ":" + this.getPort();
	}
	
	final public boolean isConnected() {
		return this.gate != null;
	}
	
	final public void setAddress(InetSocketAddress addr) {
		this.address = addr;
	}
	
	final public void setPort(int port) {
		this.address = new InetSocketAddress(this.address.getAddress(), port);
	}
	
	final public NetworkGate getGate() {
		return this.gate;
	}
	
	final public void setGate(NetworkGate gate) {
		this.gate = gate;
	}

}

