/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolServer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:10:31 PM
////////

package net.kerious.engine.network.client;

import java.net.InetAddress;
import java.net.SocketException;

import me.corsin.javatools.misc.ValueHolder;
import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.utils.TemporaryUpdatableArray;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class ServerService extends AbstractKeriousProtocolService implements PeerServerDelegate {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private IntMap<ClientPeer> peersAsMap;
	final private TemporaryUpdatableArray<ClientPeer> peersAsArray;
	final private ValueHolder<String> refuseConnectionReasonVH;
	private int playerIdSequence;
	private ServerServiceDelegate delegate;
	private ServerServiceListener listener;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ServerService() throws SocketException {
		this(0);
	}

	public ServerService(int port) throws SocketException {
		super(port);
		
		this.peersAsMap = new IntMap<ClientPeer>();
		this.peersAsArray = new TemporaryUpdatableArray<ClientPeer>(ClientPeer.class);
		this.refuseConnectionReasonVH = new ValueHolder<String>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		ClientPeer[] peers = this.peersAsArray.begin();
		
		for (int i = 0, length = this.peersAsArray.size; i < length; i++) {
			ClientPeer peer = peers[i];
			
			if (!peer.hasExpired()) {
				peer.update(deltaTime);
			} else {
				this.removePeer(peer);
			}
		}
		
		this.peersAsArray.end();
	}
	
	final private ClientPeer getPeer(InetAddress address, int port) {
		int hashCode = KeriousProtocolPeer.computeHashCodeForAddress(address, port);

		return this.peersAsMap.get(hashCode);
	}
	
	/**
	 * Create a peer that represents a connection between this client and the server
	 * @param address
	 * @param port
	 * @return
	 */
	protected ClientPeer createPeer(InetAddress address, int port) {
		return new ClientPeer(address, port);
	}
	
	private ClientPeer addPeer(String name, InetAddress address, int port) {
		int hashCode = KeriousProtocolPeer.computeHashCodeForAddress(address, port);

		ClientPeer peer = this.peersAsMap.get(hashCode);
		
		if (peer == null) {
			this.playerIdSequence++;
			
			peer = this.createPeer(address, port);
			peer.setPlayerId(this.playerIdSequence);
			peer.setProtocol(this.getProtocol());
			peer.setGate(this.getGate());
			peer.setDelegate(this);
			peer.setName(name);
			
			this.peersAsArray.add(peer);
			this.peersAsMap.put(hashCode, peer);
			
			if (this.listener != null) {
				this.listener.onPeerConnected(this, peer);
			}
		}
		
		return peer;
	}
	
	private void removePeer(ClientPeer peer) {
		this.peersAsArray.removeValue(peer, true);
		this.peersAsMap.remove(peer.hashCode());
		
		if (this.listener != null) {
			this.listener.onPeerDisconnected(this, peer);
		}
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
	
	final private void handleReceivedPacketFromKnownPeer(KeriousProtocolPeer peer, KeriousPacket packet) {
		peer.handlePacketReceived(packet);
	}
	
	final private void handleReceivedPacketFromUnknownPeer(InetAddress address, int port, KeriousPacket packet) {
		if (packet.packetType == KeriousPacket.TypeConnection) {
			ConnectionPacket connectionPacket = (ConnectionPacket)packet;
			
			switch (connectionPacket.connectionRequest) {
			case ConnectionPacket.ConnectionAsk:
				boolean connectionAccepted = false;
				if (this.delegate != null) {
					this.refuseConnectionReasonVH.setValue(null);
					connectionAccepted = this.delegate.shouldAcceptConnection(this, address.getHostAddress(), port, this.refuseConnectionReasonVH);
				}
				
				if (connectionAccepted) {
					ClientPeer peer = this.addPeer(connectionPacket.playerName, address, port);
					peer.handlePacketReceived(connectionPacket);
				} else {
					ConnectionPacket responseConnectionPacket = this.protocol.createConnectionPacket(ConnectionPacket.ConnectionInterrupted);
					responseConnectionPacket.reason = this.refuseConnectionReasonVH.value();
					this.gate.send(responseConnectionPacket, address, port);
					responseConnectionPacket.release();
				}
				
				break;
			case ConnectionPacket.ConnectionAccepted:
			case ConnectionPacket.ConnectionInterrupted:
				// Doesn't make sense as the host is not recognized
				break;
			}
		}
	}
	
	@Override
	public void fillWorldInformations(ClientPeer peer, ObjectMap<String, String> informations) {
		if (this.delegate != null) {
			this.delegate.fillWorldInformations(this, informations);
		}
	}
	
	@Override
	public void updateWorldWithCommands(ClientPeer peer, float directionAngle, float directionStrength, int actions) {
		if (this.delegate != null) {
			this.delegate.updateWorldWithCommands(this, peer.getPlayerId(), directionAngle, directionStrength, actions);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public ServerServiceDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(ServerServiceDelegate delegate) {
		this.delegate = delegate;
	}
	
	public Array<ClientPeer> getPeers() {
		return this.peersAsArray;
	}

	public ServerServiceListener getListener() {
		return listener;
	}

	public void setListener(ServerServiceListener listener) {
		this.listener = listener;
	}
}
