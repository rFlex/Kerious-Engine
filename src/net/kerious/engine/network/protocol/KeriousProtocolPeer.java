/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousPeer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 8:17:40 PM
////////

package net.kerious.engine.network.protocol;

import java.net.InetAddress;

import net.kerious.engine.network.peer.NetworkPeer;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.KeriousReliablePacket;
import net.kerious.engine.utils.TemporaryUpdatable;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class KeriousProtocolPeer extends NetworkPeer implements TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final int MAX_CONNECTION_ATTEMPTS = 3;
	public static final float CONNECTION_ATTEMPT_WAITING_TIME = 1f;
	public static final int ACK_SIZE = 32;
	
	private KeriousProtocol protocol;
	private boolean connected;
	private boolean expired;
	private boolean packetSentThisFrame;
	private int connectionAttempt;
	private float nextConnectionAttempt;
	private int sequence;
	private int lastSequenceReceived;
	private int ack;
	private KeriousReliablePacket[] sentPackets;
	private KeriousProtocolPeerListener listener;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocolPeer(InetAddress address, int port) {
		super(address, port, null);
	}
	
	public KeriousProtocolPeer(String ip, int port) {
		super(ip, port);
	}
	
	public KeriousProtocolPeer(NetworkPeer networkPeer) {
		super(networkPeer);
	}
	
	@Override
	protected void commonInit() {
		super.commonInit();
		
		this.sentPackets = new KeriousReliablePacket[ACK_SIZE];
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void update(float deltaTime) {
		if (!this.connected) {
			nextConnectionAttempt -= deltaTime;
			if (nextConnectionAttempt <= 0) {
				if (connectionAttempt == MAX_CONNECTION_ATTEMPTS) {
					if (this.listener != null) {
						this.listener.onConnectionFailed(this);
					}
					this.expired = true;
				} else {
					this.sendConnectionPacket();
					nextConnectionAttempt = CONNECTION_ATTEMPT_WAITING_TIME;
					connectionAttempt++;
				}
			}
		} else {
			if (!this.packetSentThisFrame) {
				this.sendKeepAlivePacket();
			}
		}
		
		this.packetSentThisFrame = false;
	}
	
	final private void addReliablePacketToSentPacket(KeriousReliablePacket packet) {
		this.sequence++;
		
		packet.ack = this.ack;
		packet.sequence = this.sequence;
		packet.lastSequenceReceived = this.lastSequenceReceived;
		
		// Shifting the sentPackets array
		KeriousReliablePacket lastPacket = packet;
		KeriousReliablePacket currentPacket = null;
		for (int i = 0; i < ACK_SIZE; i++) {
			currentPacket = this.sentPackets[i];
			this.sentPackets[i] = lastPacket;
			lastPacket = currentPacket;
		}
		
		if (lastPacket != null) {
			this.packetLost(lastPacket);
			lastPacket.release();
		}
		
		packet.retain();
	}
	
	@Override
	public void send(Object packet) {
		KeriousPacket keriousPacket = (KeriousPacket)packet;
		
		if (keriousPacket.childPacket instanceof KeriousReliablePacket) {
			this.addReliablePacketToSentPacket((KeriousReliablePacket)keriousPacket.childPacket);
		}
		
		this.packetSentThisFrame = true;
		
		super.send(packet);
	}
	
	private void sendKeepAlivePacket() {
		KeriousPacket packet = this.protocol.createKeepAlivePacket();

		this.send(packet);
		
		packet.release();
	}
	
	private void sendConnectionPacket() {
		KeriousPacket packet = this.protocol.createConnectionPacket(ConnectionPacket.CONNECTION_ASK);
		
		this.send(packet);
		
		packet.release();
	}
	
	public void cancelConnectionAttempt() {
		this.expired = true;
		this.connectionAttempt = 0;
	}
	
	public void initiateConnection() {
		this.connected = false;
		this.connectionAttempt = 0;
		this.nextConnectionAttempt = 0;
	}

	final private void updateAck(int sequence) {
		int lastSequence = this.lastSequenceReceived;
		
		int offset = sequence - lastSequence;
		
		if (offset < ACK_SIZE) {
			int oldAck = this.ack;
			int newAck = (oldAck << offset) | 0x1 << offset - 1;
			
			this.ack = newAck;
		} else {
			// We didn't receive any packet for a long time, the ack is null
			this.ack = 0;
		}
		
		this.lastSequenceReceived = sequence;
	}
	
	final private void analyzeAck(int lastSequenceReceive, int ack) {
		int offset = this.sequence - lastSequenceReceive;
		int ackOffset = -1;
		
		while (offset < ACK_SIZE) {
			KeriousReliablePacket packet = this.sentPackets[offset];
			
			if (packet != null) {
				boolean packetReceived;
				
				if (ackOffset == -1) {
					// The last sent packet IS the last received packet
					packetReceived = true;
				} else {
					packetReceived = ((ack >>> ackOffset) & 0x1) == 0x1;
				}
				
				if (packetReceived) {
					this.packetReceived(packet);
				} else {
					this.packetLost(packet);
				}
				 
				this.sentPackets[offset] = null;
				packet.release();
			}
			
			ackOffset++;
			offset++;
		}
	}
	
	/**
	 * Called when the packet has been received by the peer
	 * @param packet
	 */
	protected void packetReceived(KeriousReliablePacket packet) {
	}
	
	/**
	 * Called when the packet has not been received by the peer
	 * @param packet
	 */
	protected void packetLost(KeriousReliablePacket packet) {
		System.out.println("Packet lost " + this.getPort());
	}
	
	protected void handleReliablePacketReceived(KeriousReliablePacket reliablePacket) {
		int remoteAck = reliablePacket.ack;
		int remoteSequence = reliablePacket.sequence;
		int lastSequenceReceived = reliablePacket.lastSequenceReceived;
		
		if (remoteSequence > this.lastSequenceReceived) {
			this.updateAck(remoteSequence);
			this.analyzeAck(lastSequenceReceived, remoteAck);
		} else {
			// The packet is late, discarding it
		}
	}
	
	public void handlePacketReceived(KeriousPacket packet) {
		KeriousSerializableData childPacket = packet.childPacket;
		
		if (packet.childPacket instanceof KeriousReliablePacket) {
			this.handleReliablePacketReceived((KeriousReliablePacket)childPacket);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public KeriousProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(KeriousProtocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public boolean hasExpired() {
		return this.expired;
	}

	public KeriousProtocolPeerListener getListener() {
		return listener;
	}

	public void setListener(KeriousProtocolPeerListener listener) {
		this.listener = listener;
	}

}
