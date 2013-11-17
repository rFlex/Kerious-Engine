/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// ModelPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:32:14 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.nio.ByteBuffer;

import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class ModelPacket extends KeriousSerializableData<ModelPacket> {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int entityType;
	public EntityModel model;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void copyTo(ModelPacket object) {
		object.entityType = this.entityType;
		object.model = this.model != null ? this.model.clone() : null;
	}

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) {
		this.entityType = buffer.getInt();
		
		this.model = protocol.createModel(this.entityType);
		
		if (this.model != null) {
			this.model.deserialize(protocol, buffer);
		}
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putInt(this.entityType);
		if (this.model != null) {
			this.model.serialize(protocol, buffer);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.entityType = 0;
		if (this.model != null) {
			this.model.release();
			this.model = null;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
