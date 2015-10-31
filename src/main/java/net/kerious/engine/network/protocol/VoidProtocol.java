/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.protocol
// VoidProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:23:49 AM
////////

package net.kerious.engine.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VoidProtocol implements INetworkProtocol {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Object deserialize(ByteBuffer byteBuffer) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serialize(Object object, ByteBuffer byteBuffer) {
		// TODO Auto-generated method stub
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
