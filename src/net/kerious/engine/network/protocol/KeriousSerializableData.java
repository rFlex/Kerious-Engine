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
	
	private StringBuilder sb = new StringBuilder();
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousSerializableData() {
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Utility method to get a string
	 * @param buffer
	 * @return
	 */
	final protected String getString(ByteBuffer buffer) throws IOException {
		this.sb.setLength(0);
		
		char c;
		while ((c = buffer.getChar()) != 0) {
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("Warning: Leaking " + this.getClass().getSimpleName() + "@" + this.hashCode());
	}

	/**
	 * Utility method to put a string
	 * @param str
	 */
	final protected void putString(ByteBuffer buffer, String str) {
		if (str != null) {
			for (int i = 0, length = str.length(); i < length; i++) {
				char c = str.charAt(i);
				buffer.putChar(c);
			}
		}
		
		buffer.putChar((char)0);
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
	
	abstract public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException;
	abstract public void serialize(KeriousProtocol protocol, ByteBuffer buffer);

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
