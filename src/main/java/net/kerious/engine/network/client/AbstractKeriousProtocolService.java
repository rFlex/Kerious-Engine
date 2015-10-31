/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.manager
// NetworkManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 5:41:36 PM
////////

package net.kerious.engine.network.client;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.SocketException;

import net.kerious.engine.network.gate.NetworkGateListener;
import net.kerious.engine.network.gate.UDPGate;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.utils.TemporaryUpdatable;

public abstract class AbstractKeriousProtocolService implements Closeable, TemporaryUpdatable, NetworkGateListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	final protected KeriousProtocol protocol;
	protected UDPGate gate;
	private boolean closed;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AbstractKeriousProtocolService() throws SocketException {
		this(0);
	}

	public AbstractKeriousProtocolService(int port) throws SocketException {
		this.protocol = new KeriousProtocol();
		this.gate = new UDPGate(this.protocol, port);
		this.gate.setListener(this);
		this.gate.start();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		if (this.gate != null) {
			this.gate.update();
		}
	}

	@Override
	public void close() {
		if (this.gate != null) {
			this.gate.close();
			this.gate = null;
		}
		this.closed = true;
	}
	
	@Override
	public void onSent(InetAddress address, int port, Object packet) {
		
	}

	@Override
	public void onFailedSend(InetAddress address, int port, Object packet, Exception exception) {
	}

	@Override
	public void onFailedReceive(InetAddress address, int port, Exception exception) {
//		exception.printStackTrace();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KeriousProtocol getProtocol() {
		return this.protocol;
	}
	
	public int getPort() {
		return this.gate != null ? this.gate.getPort() : -1;
	}

	@Override
	public boolean hasExpired() {
		return this.closed;
	}
	
	public UDPGate getGate() {
		return this.gate;
	}
}
