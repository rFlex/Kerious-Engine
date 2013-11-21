/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolServerDelegate.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:24:20 PM
////////

package net.kerious.engine.network.client;

import me.corsin.javatools.misc.ValueHolder;

public interface KeriousProtocolServerDelegate {
	
	boolean shouldAcceptConnection(KeriousProtocolServer server, String ip, int port, ValueHolder<String> outReason);

}
