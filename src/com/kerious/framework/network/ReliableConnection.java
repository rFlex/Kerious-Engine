/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network
// Client.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 2:54:09 PM
////////

package com.kerious.framework.network;

import java.util.Iterator;
import java.util.LinkedList;

import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.AsyncLinkedList;

public class ReliableConnection {

	////////////////////////
	// VARIABLES
	////////////////

	public static final int ACK_SIZE = 32;
	private NetworkPeer peer;
	private IPacketListener packetListener;
	private short identifier;
	private short code;
	private int currentAck;
	private int lastReceivedSequence;
	private int currentSequenceNumber;
	private int receivedPacketsCount;
	private int sentPacketsCount;
	private int sentPacketsLostCount;
	private int sentPacketsReceivedCount;
	private int ping;
	private AsyncLinkedList<KeriousReliableUDPPacket> sentPackets;
	private LinkedList<KeriousReliableUDPPacket> receivedPackets;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ReliableConnection(short identifier) {
		this.identifier = identifier;
	}
	
	public ReliableConnection(short identifier, short code, NetworkPeer peer) {
		this.setNetworkpeer(peer);
		
		this.identifier = identifier;
		this.code = code;
		this.currentSequenceNumber = 10;

		this.sentPackets = new AsyncLinkedList<KeriousReliableUDPPacket>();
		this.receivedPackets = new LinkedList<KeriousReliableUDPPacket>();
	}

	////////////////////////
	// METHODS
	////////////////

	public void clearPackets() {
		this.receivedPackets.clear();
	}
	
	private void updateAck(int packetSequenceNumber) {
		int newAck = 0;
		
		Iterator<KeriousReliableUDPPacket> it = this.receivedPackets.iterator();

		while (it.hasNext()) {
			KeriousReliableUDPPacket packet = it.next();
			
			final int packetDecal = packetSequenceNumber - packet.getSequenceNumber();
			
			// Check if the packet is too old
			if (packetDecal <= ACK_SIZE) {
				newAck |= 1 << (ACK_SIZE - packetDecal);
			} else {
				it.remove();
			}
		}

		this.currentAck = newAck;
		this.lastReceivedSequence = packetSequenceNumber;
	}
	
	private void checkForLostPacket(int lastReceivedSequence, int packetAck) {
		this.sentPackets.lock();
		Iterator<KeriousReliableUDPPacket> it = this.sentPackets.iterator();
		
		while (it.hasNext()) {
			KeriousReliableUDPPacket packet = it.next();
			final int packetSequence = packet.getSequenceNumber();
			final int packetDecal = lastReceivedSequence - packetSequence;

			if (packetDecal >= 0) {
				boolean packetLost = true;
				
				if (packetDecal == 0) {
					packetLost = false;
				} else if (packetDecal <= ACK_SIZE) {
					packetLost = (packetAck & (1 << (ACK_SIZE - packetDecal))) == 0;
				}
				
				it.remove();
				
				if (packetLost) {
					this.sentPacketsReceivedCount++;
					this.packetListener.onSendPacketLost(this, packet);
				} else {
					this.ping = (int)(System.currentTimeMillis() - packet.getTimestamp());
					this.sentPacketsLostCount++;
					this.packetListener.onSendPacketReceived(this, packet);
				}
			}
			
		}
		this.sentPackets.unlock();
	}
	
	private void fillPacket(KeriousReliableUDPPacket packet) {
		packet.setIdent(this.identifier);
		packet.setCode(this.code);
		packet.setSequenceNumber(this.currentSequenceNumber);
		packet.setLastSequenceReceived(this.lastReceivedSequence);
		packet.setAck(this.currentAck);
		packet.setTimestamp(System.currentTimeMillis());

		this.sentPackets.add(packet);
		
		this.currentSequenceNumber++;
	}
	
	public void send(KeriousReliableUDPPacket packet) {
		if (this.peer == null) {
			throw new KeriousException("Cannot send a packet if no peer was set in the ReliableConnection");
		}
		this.fillPacket(packet);
		this.peer.send(packet);
	}
	
	public void send(KeriousUDPPacket packet) {
		if (packet.reliable) {
			this.send((KeriousReliableUDPPacket)packet);
		} else {
			if (this.peer == null) {
				throw new KeriousException("Cannot send a packet if no peer was set in the ReliableConnection");
			}
			this.peer.send(packet);
		}
	}
	
	public void addToReceivedPacket(KeriousReliableUDPPacket packet) {
		final int packetSequenceNumber = packet.getSequenceNumber();
		
		// Check if the packet should be discarded
		if (packetSequenceNumber > lastReceivedSequence) {
			this.updateAck(packetSequenceNumber);
			this.receivedPackets.addFirst(packet);
			
			this.checkForLostPacket(packet.getLastReceivedSequence(), packet.getAck());
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final short getIdentifier() {
		return this.identifier;
	}
	
	public final short getCode() {
		return this.code;
	}
	
	public final boolean isConnected() {
		return this.peer != null;
	}
	
	public final NetworkPeer getNetworkPeer() {
		return this.peer;
	}
	
	public final void setNetworkpeer(NetworkPeer peer) {
		this.peer = peer;
	}

	public final IPacketListener getPacketListener() {
		return packetListener;
	}

	public final void setPacketListener(IPacketListener packetListener) {
		this.packetListener = packetListener;
	}

	public final int getReceivedPacketsCount() {
		return receivedPacketsCount;
	}

	public final int getSentPacketsCount() {
		return sentPacketsCount;
	}

	public final int getSentPacketsLostCount() {
		return sentPacketsLostCount;
	}

	public final int getSentPacketsReceivedCount() {
		return sentPacketsReceivedCount;
	}

	public final int getPing() {
		return ping;
	}
}
