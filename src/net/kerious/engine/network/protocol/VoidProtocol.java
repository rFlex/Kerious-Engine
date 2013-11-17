/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.protocol
// VoidProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:23:49 AM
////////

package net.kerious.engine.network.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import me.corsin.javatools.io.IOUtils;

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
	public Object deserialize(InputStream inputStream) throws IOException {
		return IOUtils.readStream(inputStream);
	}

	@Override
	public InputStream serialize(Object object) {
		if (object instanceof InputStream) {
			return (InputStream)object;
		}
		if (object instanceof byte[]) {
			return new ByteArrayInputStream((byte[])object);
		}
		if (object instanceof String) {
			return new ByteArrayInputStream(((String)object).getBytes());
		}
		
		return null;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
