/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// Packet.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 1:45:09 PM
////////

package net.kerious.engine.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

import me.corsin.javatools.misc.PoolableImpl;

public abstract class KeriousSerializableData extends PoolableImpl {

	////////////////////////
	// VARIABLES
	////////////////
	
	public int retainCount;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousSerializableData() {
	}

	////////////////////////
	// METHODS
	////////////////
	
	public KeriousSerializableData clone() {
		KeriousSerializableData element = (KeriousSerializableData)this.getPool().obtain();
		
		this.copyTo(element);
		
		return element;
	}
	
	@Override
	public void reset() {
		this.retainCount = 1;
	}
	
	public void retain() {
		this.retainCount++;
	}
	
	@Override
	public void release() {
		this.retainCount--;
		
		if (this.retainCount == 0) {
			super.release();
		}
	}
	
	abstract public void copyTo(KeriousSerializableData object);
	abstract public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException;
	abstract public void serialize(KeriousProtocol protocol, ByteBuffer buffer);

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
