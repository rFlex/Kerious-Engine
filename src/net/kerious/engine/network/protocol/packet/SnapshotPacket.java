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

import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.protocol.KeriousProtocol;

import com.badlogic.gdx.utils.Array;

public class SnapshotPacket extends KeriousReliablePacket {

	////////////////////////
	// VARIABLES
	////////////////

	private Array<EntityModel> models;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public SnapshotPacket() {
		super(SNAPSHOT_TYPE);
		this.models = new Array<EntityModel>(64);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void addModel(EntityModel model) {
		model.retain();
		
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

		EntityModel[] models = this.models.items;
		for (int i = 0, length = this.models.size; i < length; i++) {
			EntityModel model = models[i];
			model.release();
			models[i] = null;
		}
		
		this.models.size = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
