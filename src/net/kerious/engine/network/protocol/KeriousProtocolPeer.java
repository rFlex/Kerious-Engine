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

import net.kerious.engine.KeriousException;
import net.kerious.engine.network.peer.NetworkPeer;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.KeriousReliablePacket;
import net.kerious.engine.utils.TemporaryUpdatable;

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
	private boolean readyToReceiveSnapshots;
	private int connectionAttempt;
	private float nextConnectionAttempt;
	private float timeout;
	private int sequence;
	private int lastSequenceReceived;
	private int ack;
	private KeriousReliablePacket[] sentPackets;
	private KeriousProtocolPeerListener listener;
	
	public int toSkip;
	
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
						this.listener.onConnectionFailed(this, new KeriousException("Connection failed after " + connectionAttempt + " retries"));
					}
					this.expired = true;
				} else {
					this.sendConnectionPacket();
					nextConnectionAttempt = CONNECTION_ATTEMPT_WAITING_TIME;
					connectionAttempt++;
				}
			}
		} else {
			this.timeout -= deltaTime;
			
			if (this.timeout < 0) {
				this.expired = true;
			} else {
				if (!this.packetSentThisFrame) {
					this.sendKeepAlivePacket();
				}
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
		
		if (keriousPacket instanceof KeriousReliablePacket) {
			this.addReliablePacketToSentPacket((KeriousReliablePacket)keriousPacket);
		}
		
		this.packetSentThisFrame = true;
		
		if (this.toSkip <= 0) {
			super.send(packet);
		} else {
			this.toSkip--;
		}
		
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
	
	final private void handleReliablePacketReceived(KeriousReliablePacket reliablePacket) {
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
	
	/**
	 * Handle the received packet, if the method returns true if the
	 * peer has found a way to handle it
	 * @param packet
	 * @return
	 */
	public boolean handlePacketReceived(KeriousPacket packet) {
		if (packet instanceof KeriousReliablePacket) {
			this.handleReliablePacketReceived((KeriousReliablePacket)packet);
		}
		
		switch (packet.packetType) {
		case KeriousPacket.CONNECTION_TYPE:
			ConnectionPacket connectionPacket = (ConnectionPacket)packet;
			
			switch (connectionPacket.connectionRequest) {
			case ConnectionPacket.CONNECTION_ASK:
				// Resend the accepted packet
				this.send(this.protocol.createConnectionPacket(ConnectionPacket.CONNECTION_RESP_ACCEPTED));
				break;
			case ConnectionPacket.CONNECTION_RESP_ACCEPTED:
				if (!this.connected) {
					this.connected = true;
					if (this.listener != null) {
						this.listener.onConnected(this);
					}
				}
				break;
			case ConnectionPacket.CONNECTION_RESP_REFUSED:
				this.cancelConnectionAttempt();
				if (this.listener != null) {
					this.listener.onConnectionFailed(this, new KeriousException("Connection refused by the remote server"));
				}
				break;
			default:
				return false;
			}
			break;
		default:
			return false;
		}
		return true;
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
	
	public float getTimeout() {
		return this.timeout;
	}
	
	public void setTimeout(float timeout) {
		this.timeout = timeout;
	}

	public boolean isReadyToReceiveSnapshots() {
		return readyToReceiveSnapshots;
	}

	public void setReadyToReceiveSnapshots(boolean readyToReceiveSnapshots) {
		this.readyToReceiveSnapshots = readyToReceiveSnapshots;
	}

	public KeriousProtocolPeerListener getListener() {
		return listener;
	}

	public void setListener(KeriousProtocolPeerListener listener) {
		this.listener = listener;
	}

}
