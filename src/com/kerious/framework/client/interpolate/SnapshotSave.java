/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.client.interpolate
// SnapshotSave.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 9, 2012 at 12:37:03 AM
////////

package com.kerious.framework.client.interpolate;

import com.kerious.framework.client.KeriousPlay;
import com.kerious.framework.network.protocol.packets.EntityState;
import com.kerious.framework.network.protocol.packets.SnapshotPacket;

public class SnapshotSave {

	////////////////////////
	// VARIABLES
	////////////////
	
	private SnapshotPacket packet;
	private long timeReceived;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public SnapshotSave(KeriousPlay keriousPlay, SnapshotPacket packet) {
		this.packet = packet;
		this.timeReceived = keriousPlay.getApplication().getCurrentTime();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void interpolateWith(SnapshotSave save, long renderingTime) {
		final float ratio = (float)(renderingTime - this.timeReceived) / (float)(save.timeReceived - this.timeReceived);

		for (EntityState state : this.packet.getEntityStates()) {
			EntityState othState = save.packet.getEntityStateForID(state.entityID);
			
			if (othState != null) {
				state.viewDirectionX -= (state.viewDirectionX - othState.viewDirectionX) * ratio;
				state.viewDirectionY -= (state.viewDirectionY - othState.viewDirectionY) * ratio;
				state.positionX -= (state.positionX - othState.positionX) * ratio;
				state.positionY -= (state.positionY - othState.positionY) * ratio;
				state.moveDirectionX -= (state.moveDirectionX - othState.moveDirectionX) * ratio;
				state.moveDirectionY -= (state.moveDirectionY - othState.moveDirectionY) * ratio;
				state.speed -= (state.speed - othState.speed) * ratio;
			}
		}
		
		this.timeReceived = renderingTime;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final long getTimeReceived() {
		return timeReceived;
	}
	
	public final SnapshotPacket getPacket() {
		return this.packet;
	}
}
