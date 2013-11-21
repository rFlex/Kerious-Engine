/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// SnapshotPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:23:16 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.world.event.Event;

import com.badlogic.gdx.utils.Array;

public class SnapshotPacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////

	private Array<EntityModel> models;
	private Array<Event> events;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public SnapshotPacket() {
		super(TypeSnapshot);
		this.models = new Array<EntityModel>(64);
		this.events = new Array<Event>(64);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void addModel(EntityModel model) {
		model.retain();
		
		this.models.add(model);
	}
	
	public void addEvent(Event event) {
		event.retain();
		
		this.events.add(event);
	}
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		int length = buffer.getInt();
		for (int i = 0; i < length; i++) {
			byte eventType = buffer.get();
			this.models.add(protocol.createEntityModel(eventType));
		}
		
		length = buffer.getInt();
		for (int i = 0; i < length; i++) {
			byte eventType = buffer.get();
			this.events.add(protocol.createEvent(eventType));
		}
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.putInt(this.models.size);
		EntityModel[] models = this.models.items;
		for (int i = 0, length = this.models.size; i < length; i++) {
			EntityModel model = models[i];
			buffer.put(model.type);
			model.serialize(protocol, buffer);
		}
		
		buffer.putInt(this.events.size);
		Event[] events = this.events.items;
		for (int i = 0, length = this.events.size; i < length; i++) {
			Event event = events[i];
			buffer.put(event.type);
			event.serialize(protocol, buffer);
		}
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
		
		Event[] events = this.events.items;
		for (int i = 0, length = this.events.size; i < length; i++) {
			Event event = events[i];
			event.release();
			events[i] = null;
		}
		
		this.events.size = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
