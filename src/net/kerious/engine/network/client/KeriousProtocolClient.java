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
import java.net.UnknownHostException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.network.gate.NetworkGateListener;
import net.kerious.engine.network.gate.UDPGate;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.KeriousProtocolPeerListener;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.utils.TemporaryUpdatable;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.SnapshotArray;

public class KeriousProtocolClient implements Closeable, TemporaryUpdatable, NetworkGateListener, KeriousProtocolPeerListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private KeriousEngine engine;
	final private IntMap<KeriousProtocolPeer> peersAsMap;
	final private SnapshotArray<KeriousProtocolPeer> peersAsArray;
	private UDPGate gate;
	private KeriousProtocol protocol;
	private KeriousProtocolClientListener listener;
	private boolean closed;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousProtocolClient(KeriousEngine engine) throws SocketException {
		this(engine, 0);
	}

	public KeriousProtocolClient(KeriousEngine engine, int port) throws SocketException {
		this.engine = engine;
		this.peersAsMap = new IntMap<KeriousProtocolPeer>();
		this.peersAsArray = new SnapshotArray<KeriousProtocolPeer>(KeriousProtocolPeer.class);
		this.protocol = new KeriousProtocol();
		this.gate = new UDPGate(this.protocol, port);
		this.gate.setListener(this);
		this.gate.start();
		
		engine.addTemporaryUpdatable(this);
	}

	////////////////////////
	// METHODS
	////////////////
	
	private KeriousProtocolPeer addPeer(String ip, int port) throws UnknownHostException {
		return this.addPeer(InetAddress.getByName(ip), port);
	}
	
	final private KeriousProtocolPeer getPeer(InetAddress address, int port) {
		int hashCode = KeriousProtocolPeer.computeHashCodeForAddress(address, port);

		return this.peersAsMap.get(hashCode);
	}
	
	private KeriousProtocolPeer addPeer(InetAddress address, int port) {
		int hashCode = KeriousProtocolPeer.computeHashCodeForAddress(address, port);

		KeriousProtocolPeer peer = this.peersAsMap.get(hashCode);
		
		if (peer == null) {
			peer = this.createPeer(address, port);
			peer.setProtocol(this.protocol);
			peer.setGate(this.gate);
			peer.setListener(this);
			
			this.peersAsArray.add(peer);
			this.peersAsMap.put(hashCode, peer);
		}
		
		return peer;
	}
	
	private void removePeer(KeriousProtocolPeer peer) {
		this.peersAsArray.removeValue(peer, true);
		this.peersAsMap.remove(peer.hashCode());
	}
	
	@Override
	public void update(float deltaTime) {
		if (this.gate != null) {
			this.gate.update();
		}
		
		KeriousProtocolPeer[] peers = this.peersAsArray.begin();
		
		for (int i = 0, length = this.peersAsArray.size; i < length; i++) {
			KeriousProtocolPeer peer = peers[i];
			
			if (!peer.hasExpired()) {
				peer.update(deltaTime);
			} else {
				this.removePeer(peer);
			}
		}
		
		this.peersAsArray.end();
	}
	
	final private void handleReceivedPacketFromKnownPeer(KeriousProtocolPeer peer, KeriousPacket packet) {
		if (packet.packetType == KeriousProtocol.CONNECTION_TYPE) {
			ConnectionPacket connectionPacket = (ConnectionPacket)packet.childPacket;
			
			switch (connectionPacket.type) {
			case ConnectionPacket.CONNECTION_ASK:
				// Resend the accepted packet
				peer.send(this.protocol.createConnectionPacket(ConnectionPacket.CONNECTION_RESP_ACCEPTED));
				break;
			case ConnectionPacket.CONNECTION_RESP_ACCEPTED:
				peer.setConnected(true);
				if (this.listener != null) {
					this.listener.onConnected(this, peer.getIP(), peer.getPort());
				}
				break;
			case ConnectionPacket.CONNECTION_RESP_REFUSED:
				peer.cancelConnectionAttempt();
				if (this.listener != null) {
					this.listener.onConnectionFailed(this, peer.getIP(), peer.getPort(), new KeriousException("Connection refused by the remote server"));
				}
				break;
			}
		} else {
			peer.handlePacketReceived(packet);
		}
	}
	
	final private void handleReceivedPacketFromUnknownPeer(InetAddress address, int port, KeriousPacket packet) {
		if (packet.packetType == KeriousProtocol.CONNECTION_TYPE) {
			ConnectionPacket connectionPacket = (ConnectionPacket)packet.childPacket;
			
			switch (connectionPacket.type) {
			case ConnectionPacket.CONNECTION_ASK:
				boolean connectionAccepted = false;
				if (this.listener != null) {
					connectionAccepted = this.listener.shouldAcceptConnection(this, address.getHostAddress(), port);
				}
				if (connectionAccepted) {
					KeriousProtocolPeer peer = this.addPeer(address, port);
					peer.setConnected(true);
					this.gate.send(this.protocol.createConnectionPacket(ConnectionPacket.CONNECTION_RESP_ACCEPTED), address, port);
					if (this.listener != null) {
						this.listener.onConnected(this, address.getHostName(), port);
					}
				} else {
					this.gate.send(this.protocol.createConnectionPacket(ConnectionPacket.CONNECTION_RESP_REFUSED), address, port);
				}
				break;
			case ConnectionPacket.CONNECTION_RESP_ACCEPTED:
			case ConnectionPacket.CONNECTION_RESP_REFUSED:
				// Doesn't make sense as the host is not recognized
				break;
			}
		}
	}
	
	public void connectTo(String ip, int port) {
		try {
			KeriousProtocolPeer peer = this.addPeer(ip, port);
			peer.initiateConnection();
		} catch (Exception e) {
			if (this.listener != null) {
				this.listener.onConnectionFailed(this, ip, port, e);
			}
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
	public void onReceived(InetAddress address, int port, Object packet) {
		KeriousProtocolPeer peer = this.getPeer(address, port);
		KeriousPacket keriousPacket = (KeriousPacket)packet;
		
		if (peer == null) {
			this.handleReceivedPacketFromUnknownPeer(address, port, keriousPacket);
		} else {
			this.handleReceivedPacketFromKnownPeer(peer, keriousPacket);
		}
		keriousPacket.release();
	}

	@Override
	public void onSent(InetAddress address, int port, Object packet) {
		
	}

	@Override
	public void onFailedSend(InetAddress address, int port, Object packet, Exception exception) {
	}

	@Override
	public void onFailedReceive(InetAddress address, int port, Exception exception) {
		System.out.println("Failed receive: " + exception.getMessage());
		exception.printStackTrace();
	}
	
	@Override
	public void onConnectionFailed(KeriousProtocolPeer peer) {
		if (this.listener != null) {
			this.listener.onConnectionFailed(this, peer.getIP(), peer.getPort(), new KeriousException("Connection failed after 3 attempts"));
		}
	}
	
	/**
	 * Override this to implement your own create KeriousProtocolPeer
	 * @param address
	 * @param port
	 * @return
	 */
	protected KeriousProtocolPeer createPeer(InetAddress address, int port) {
		return new KeriousProtocolPeer(address, port);
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

	public KeriousProtocolClientListener getListener() {
		return listener;
	}

	public void setListener(KeriousProtocolClientListener listener) {
		this.listener = listener;
	}
}
