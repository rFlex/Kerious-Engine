/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// SnapshotPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:23:16 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

import com.badlogic.gdx.utils.Array;

public class SnapshotPacket extends KeriousReliablePacket<SnapshotPacket> {

	////////////////////////
	// VARIABLES
	////////////////

	private Array<ModelPacket> models;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public SnapshotPacket() {
		this.models = new Array<ModelPacket>(64);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void addModel(ModelPacket model) {
		this.models.add(model);
	}
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) {
		
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		
	}
	
	@Override
	public void reset() {
		super.reset();
		
		for (int i = 0, length = this.models.size; i < length; i++) {
			ModelPacket model = (ModelPacket)this.models.get(i);
			model.release();
		}
		
		this.models.clear();
	}

	@Override
	public void copyTo(SnapshotPacket object) {
		super.copyTo(object);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
