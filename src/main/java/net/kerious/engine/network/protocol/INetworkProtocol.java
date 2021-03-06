/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.protocol
// INetworkProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:22:52 AM
////////

package net.kerious.engine.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface INetworkProtocol {

	Object deserialize(ByteBuffer byteBuffer) throws IOException;
	void serialize(Object object, ByteBuffer byteBuffer);
	
}
