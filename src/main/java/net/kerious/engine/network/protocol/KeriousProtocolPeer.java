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
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.utils.TemporaryUpdatable;

public class KeriousProtocolPeer extends NetworkPeer implements TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final float TimeoutTime = 3;
	public static final int AckSize = 32;
	
	protected KeriousProtocol protocol;
	private boolean expired;
	private float timeout;
	private int sequence;
	private int lastSequenceReceived;
	private int ack;
	private KeriousPacket[] sentPackets;
	private String disconnectReason;
	
	public int toSkip;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocolPeer(InetAddress address, int port) {
		super(address, port, null);
		
		this.sentPackets = new KeriousPacket[AckSize];
		this.timeout = TimeoutTime;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	public void update(float deltaTime) {
		this.timeout -= deltaTime;
		
		if (this.timeout < 0) {
			this.expired = true;
			this.disconnectReason = "Connection timed out";
		}
	}
	
	final private void addPacketToSentPacket(KeriousPacket packet) {
		this.sequence++;
		
		packet.ack = this.ack;
		packet.sequence = this.sequence;
		packet.lastSequenceReceived = this.lastSequenceReceived;
		
		// Shifting the sentPackets array
		KeriousPacket lastPacket = packet;
		KeriousPacket currentPacket = null;
		for (int i = 0; i < AckSize; i++) {
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
		
		this.addPacketToSentPacket(keriousPacket);
		
		if (this.toSkip <= 0) {
			super.send(packet);
		} else {
			this.toSkip--;
		}
		
	}
	
	public void sendKeepAlivePacket() {
		KeriousPacket packet = this.protocol.createKeepAlivePacket();

		this.send(packet);
		
		packet.release();
	}
	
	final private void updateAck(int sequence) {
		int lastSequence = this.lastSequenceReceived;
		
		int offset = sequence - lastSequence;
		
		if (offset < AckSize) {
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
		
		while (offset < AckSize) {
			KeriousPacket packet = this.sentPackets[offset];
			
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
	protected void packetReceived(KeriousPacket packet) {
	}
	
	/**
	 * Called when the packet has not been received by the peer
	 * @param packet
	 */
	protected void packetLost(KeriousPacket packet) {
		if ((packet.options & KeriousPacket.OptionResendIfLost) == 0x1) {
			this.send(packet);
		}
	}
	
	final private boolean updateAckInformations(KeriousPacket reliablePacket) {
		int remoteAck = reliablePacket.ack;
		int remoteSequence = reliablePacket.sequence;
		int lastSequenceReceived = reliablePacket.lastSequenceReceived;
		
		if (remoteSequence > this.lastSequenceReceived) {
			this.updateAck(remoteSequence);
			this.analyzeAck(lastSequenceReceived, remoteAck);
			return true;
		}
		return false;
	}
	
	/**
	 * Handle the received packet, if the method returns true if the
	 * peer has found a way to handle it
	 * @param packet
	 * @return
	 */
	public boolean handlePacketReceived(KeriousPacket packet) {
		this.timeout = TimeoutTime;
		if (!this.updateAckInformations(packet)) {
			return true;
		}
		
		return false;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

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
	
	public void setExpired(boolean value) {
		this.expired = value;
	}

	public float getTimeout() {
		return this.timeout;
	}
	
	public void setTimeout(float timeout) {
		this.timeout = timeout;
	}
	
	public void setDisconnectReason(String value) {
		this.disconnectReason = value;
	}

	public String getDisconnectReason() {
		return disconnectReason;
	}
}
