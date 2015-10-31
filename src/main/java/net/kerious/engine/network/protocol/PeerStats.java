/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// PeerStats.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 25, 2013 at 3:02:16 PM
////////

package net.kerious.engine.network.protocol;

public class PeerStats {

	////////////////////////
	// VARIABLES
	////////////////
	
	private int totalPacketsLost;
	private int totalPacketsReceived;
	private int totalPacketsSent;
	private int currentChokeRate;
	private int currentLossRate;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public PeerStats() {
		
	}

	////////////////////////
	// METHODS
	////////////////
	
	final public void packetChoked() {
		this.totalPacketsLost++;
	}
	
	final public void packetReceived() {
		this.totalPacketsReceived++;
	}
	
	final public void packetLost() {
		this.totalPacketsLost++;
	}
	
	final public void packetSent() {
		this.totalPacketsSent++;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final int getTotalPacketsLost() {
		return totalPacketsLost;
	}

	public final int getTotalPacketsReceived() {
		return totalPacketsReceived;
	}

	public final int getTotalPacketsSent() {
		return totalPacketsSent;
	}

	public final int getCurrentChokeRate() {
		return currentChokeRate;
	}

	public final int getCurrentLossRate() {
		return currentLossRate;
	}
}
