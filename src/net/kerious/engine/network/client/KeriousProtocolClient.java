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
import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.network.gate.UDPGate;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.utils.TemporaryUpdatable;

public class KeriousProtocolClient implements Closeable, TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private KeriousEngine engine;
	private UDPGate gate;
	private KeriousProtocol protocol;
	private boolean closed;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousProtocolClient(KeriousEngine engine) throws SocketException {
		this(engine, 0);
	}

	public KeriousProtocolClient(KeriousEngine engine, int port) throws SocketException {
		this.engine = engine;
		this.protocol = new KeriousProtocol();
		this.gate = new UDPGate(this.protocol, port);
		this.gate.setCallBackTaskQueue(engine.getTaskQueue());
//		this.gate.setListener(this);
		
		engine.addTemporaryUpdatable(this);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		
	}
//	
//	final private void handleReceivedPacketFromKnownPeer(KeriousProtocolPeer peer, KeriousPacket packet) {
//		
//	}
//	
//	final private void handleReceivedPacketFromUnknownPeer(NetworkPeer peer, KeriousPacket packet) {
//		
//	}
	
	public void connectTo(String ip, int port) {
//		this.gate.register(peer);
		
//		ConnectionPacket connectionPacket = (ConnectionPacket)this.protocol.createPacket(KeriousProtocol.CONNECTION_TYPE);
//		this.gate.send(connectionPacket, ip, port);
	}
	
	@Override
	public void close() {
		if (this.gate != null) {
			this.gate.close();
			this.gate = null;
		}
		this.closed = true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KeriousProtocol getProtocol() {
		return this.protocol;
	}
	
	public EntityManager getEntityManager() {
		return this.protocol.getEntityManager();
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.protocol.setEntityManager(entityManager);
	}
	
	public int getPort() {
		return this.gate != null ? this.gate.getPort() : -1;
	}

	@Override
	public boolean hasExpired() {
		return this.closed;
	}
	
	public KeriousEngine getEngine() {
		return this.engine;
	}

}
