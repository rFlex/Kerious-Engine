/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.peer
// NetworkPeer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:37:35 AM
////////

package net.kerious.engine.network.peer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.kerious.engine.network.gate.NetworkGate;
import net.kerious.engine.network.gate.NetworkPeerException;

public class NetworkPeer {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private InetAddress address;
	final private int port;
	private NetworkGate gate;
	private int cachedHashCode;
	private boolean hashCodeComputed;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public NetworkPeer(NetworkPeer networkPeer) {
		this(networkPeer.address, networkPeer.port, networkPeer.gate);
	}
	
	public NetworkPeer(String ip, int port) {
		this(ip, port, null);
	}
	
	public NetworkPeer(String ip, int port, NetworkGate gate) {
		this(new InetSocketAddress(ip, port), gate);
	}
	
	public NetworkPeer(InetSocketAddress address) {
		this(address, null);
	}
	
	public NetworkPeer(InetSocketAddress address, NetworkGate gate) {
		this(address.getAddress(), address.getPort(), gate);
	}
	
	public NetworkPeer(InetAddress address, int port, NetworkGate gate) {
		this.address = address;
		this.port = port;
		this.gate = gate;
		
		if (this.address == null) {
			throw new NullPointerException("address");
		}
		
		this.commonInit();
	}
	
	protected void commonInit() {
		
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void send(Object packet) {
		if (this.gate == null) {
			throw new NetworkPeerException("Cannot send a packet to a NetworkPeer that is not registered to a NetworkGate");
		}
		this.gate.send(packet, this.address, this.port);
	}
	
	public static int computeHashCodeForAddress(InetSocketAddress address) {
		return computeHashCodeForAddress(address.getAddress().getAddress(), address.getPort());
	}
	
	public static int computeHashCodeForAddress(InetAddress address, int port) {
		return computeHashCodeForAddress(address.getAddress(), port);
	}
	
	public static int computeHashCodeForAddress(byte[] ip, int port) {
		long value = 0;
		
		if (ip != null) {
			for (int i = 0; i < ip.length; i++) {
				byte b = ip[i];
				value |= b << (i * 8);
			}
		}
		
		value |= ((long)port) << 32;
		
		return (int)(value ^ (value >>> 32));
	}
	
	@Override
	public int hashCode() {
		if (!this.hashCodeComputed) {
			this.cachedHashCode = computeHashCodeForAddress(this.address, this.port); 
		}
		
		return this.cachedHashCode;
	}
	
	@Override
	public String toString() {
		return this.getIP() + ":" + this.getPort();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isRegisterable() {
		return this.address.getAddress() != null;
	}

	public NetworkGate getGate() {
		return gate;
	}

	public void setGate(NetworkGate gate) {
		this.gate = gate;
	}

	public String getIP() {
		return this.address.getHostAddress();
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

}
