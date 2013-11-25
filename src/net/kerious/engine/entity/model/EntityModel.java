/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity.model
// EntityModel.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:22:34 PM
////////

package net.kerious.engine.entity.model;

import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public class EntityModel extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////
	
	public byte type;
	public int id;
	public int parentId;
	public int playerId;
	public short skinId;
	public float x;
	public float y;
	public float width;
	public float height;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityModel() {
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void reset() {
		super.reset();
		
		this.type = 0;
		this.id = 0;
		this.parentId = 0;
		this.playerId = 0;
		this.skinId = 0;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) {
		this.id = buffer.getInt();
		this.parentId = buffer.getInt();
		this.playerId = buffer.getInt();
		this.skinId = buffer.getShort();
		this.x = buffer.getFloat();
		this.y = buffer.getFloat();
		this.width = buffer.getFloat();
		this.height = buffer.getFloat();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putInt(this.id);
		buffer.putInt(this.parentId);
		buffer.putInt(this.playerId);
		buffer.putShort(this.skinId);
		buffer.putFloat(this.x);
		buffer.putFloat(this.y);
		buffer.putFloat(this.width);
		buffer.putFloat(this.height);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
}
