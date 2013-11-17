/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.gate
// ReadPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 3:08:19 PM
////////

package net.kerious.engine.network.gate;

import java.io.InputStream;
import java.net.InetSocketAddress;

public class ReadPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private InetSocketAddress socketAddress;
	final private InputStream inputStream;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ReadPacket(InetSocketAddress socketAddress, InputStream inputStream) {
		this.socketAddress = socketAddress;
		this.inputStream = inputStream;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
