/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// UserListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 23, 2012 at 11:39:14 AM
////////

package com.kerious.framework.server;

import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.packets.SnapshotPacket;

public interface UserListener {

	void updateLogic();
	void willSendSnapshot(SnapshotPacket packet);
	void onDisconnected();
	void onPacketReceived(KeriousReliableUDPPacket packet);
	
}
