/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.gate
// IGateListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:20:51 AM
////////

package net.kerious.engine.network.gate;

import java.net.InetAddress;

public interface INetworkGateListener {

	void onReceived(InetAddress address, int port, Object packet);
	void onSent(InetAddress address, int port, Object packet);
	void onFailedSend(InetAddress address, int port, Object packet, Exception exception);
	void onFailedReceive(InetAddress address, int port, Exception exception);
	
}
