/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol.packet
// LoadingInformationsPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 2:28:12 PM
////////

package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class WorldInformationsPacket extends KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public ObjectMap<String, String> informations;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public WorldInformationsPacket() {
		super(TypeWorldInformations);
		
		this.informations = new ObjectMap<String, String>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void reset() {
		super.reset();
		
		this.informations.clear();
	}
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
	
		int size = buffer.getInt();
		
		for (int i = 0; i < size; i++) {
			String key = this.getString(buffer);
			String value = this.getString(buffer);
			this.informations.put(key, value);
		}
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.putInt(this.informations.size);

		for (Entry<String, String> entry : this.informations.entries()) {
			this.putString(buffer, entry.key);
			this.putString(buffer, entry.value);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
