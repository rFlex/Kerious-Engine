/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.client
// Interpolator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 9, 2012 at 12:23:53 AM
////////

package com.kerious.framework.client.interpolate;

import java.util.LinkedList;

import com.kerious.framework.client.KeriousPlay;
import com.kerious.framework.events.GameEvent;
import com.kerious.framework.network.protocol.packets.EntityState;
import com.kerious.framework.network.protocol.packets.SnapshotPacket;

public class Interpolator {

	////////////////////////
	// VARIABLES
	////////////////

	public static final int DEFAULT_INTERPOLATE = 100;
	private int interpolate;
	private SnapshotSave lastSave;
	private LinkedList<SnapshotSave> pendingSave;
	private KeriousPlay keriousPlay;
	private SnapshotPacket lastReceivedSnapshot;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Interpolator(KeriousPlay keriousPlay) {
		this.interpolate = DEFAULT_INTERPOLATE;
		this.keriousPlay = keriousPlay;
		this.pendingSave = new LinkedList<SnapshotSave>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void flush() {
		long renderingTime = this.keriousPlay.getApplication().getCurrentTime() - interpolate;

		boolean foundOldSave = true;
		
		while (foundOldSave) {
			foundOldSave = false;
			
			if (!this.pendingSave.isEmpty()) {
				final SnapshotSave packet = this.pendingSave.getFirst();
				
				if (renderingTime >= packet.getTimeReceived()) {
					this.pendingSave.removeFirst();
					
					for (GameEvent event : packet.getPacket().getEvents()) {
						this.keriousPlay.handleEvent(event);
					}
					
					this.keriousPlay.getPlayersManager().setPlayers(packet.getPacket().getPlayers());
					
					if (this.lastSave != null) {
						this.lastSave.getPacket().release();
						this.lastSave = null;
					}
					
					this.lastSave = packet;
					foundOldSave = true;
				}
			}
		}
		
		if (this.lastSave != null) {
			if (!this.pendingSave.isEmpty()) {
				this.lastSave.interpolateWith(this.pendingSave.getFirst(), renderingTime);
			}
			
			for (EntityState state : this.lastSave.getPacket().getEntityStates()) {
				this.keriousPlay.handleEntityState(state, renderingTime);
			}
		}
	}
	
	public void handleSnapshot(SnapshotPacket packet) {
		
		if (this.lastReceivedSnapshot != null) {
			packet.decompress(this.lastReceivedSnapshot);
		}
		this.lastReceivedSnapshot = packet;
		
		this.pendingSave.addLast(new SnapshotSave(this.keriousPlay, packet));
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final int getInterpolate() {
		return interpolate;
	}

	public final void setInterpolate(int interpolate) {
		this.interpolate = interpolate;
	}

}
