/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.gate
// SendTask.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 8:18:15 PM
////////

package net.kerious.engine.network.gate;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import me.corsin.javatools.misc.PoolableImpl;

public class NetworkTask extends PoolableImpl {

	////////////////////////
	// VARIABLES
	////////////////
	
	final public NetworkGate gate; 
	public InetAddress address;
	public Exception thrownException;
	public ByteBuffer buffer;
	public Object packet;
	public int port;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetworkTask(NetworkGate gate) {
		this.gate = gate;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void reset() {
		this.address = null;
		this.thrownException = null;
		
		if (this.buffer != null) {
			this.gate.getBufferPool().release(this.buffer);
			this.buffer = null;
		}
		this.packet = null;
		this.port = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
