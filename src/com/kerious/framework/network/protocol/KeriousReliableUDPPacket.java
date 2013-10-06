/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// KeriousUDPPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 3:05:05 PM
////////

package com.kerious.framework.network.protocol;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

public abstract class KeriousReliableUDPPacket extends KeriousUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////

	private short ident;
	private short code;
	private int sequenceNumber;
	private int lastSequenceReceived;
	private int ack;
	private long timestamp;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousReliableUDPPacket(byte packetType) {
		super(packetType, true);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void reset() {
		super.reset();
		
		this.ident = 0;
		this.code = 0;
		this.sequenceNumber = 0;
		this.lastSequenceReceived = 0;
		this.ack = 0;
	}
	
	protected void childUnpack() {
		this.ident = read(this.ident);
		this.code = read(this.code);
		this.sequenceNumber = read(this.sequenceNumber);
		this.lastSequenceReceived = read(this.lastSequenceReceived);
		this.ack = read(this.ack);
	}
	
	protected void childPack() {
		write(this.ident);
		write(this.code);
		write(this.sequenceNumber);
		write(this.lastSequenceReceived);
		write(this.ack);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int size() {
		return super.size() + sizeof(ident) + sizeof(code) + sizeof(this.sequenceNumber) + sizeof(this.lastSequenceReceived) + sizeof(this.ack);
	}
	
	public final short getIdent() {
		return ident;
	}

	public void setIdent(short ident) {
		this.ident = ident;
	}

	public final short getCode() {
		return code;
	}

	public void setCode(short code) {
		this.code = code;
	}
	
	public final int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public final int getLastReceivedSequence() {
		return this.lastSequenceReceived;
	}
	
	public void setLastSequenceReceived(int lastSequenceReceived) {
		this.lastSequenceReceived = lastSequenceReceived;
	}
	
	public final int getAck() {
		return this.ack;
	}
	
	public void setAck(int ack) {
		this.ack = ack;
	}

	public final long getTimestamp() {
		return timestamp;
	}

	public final void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
