/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network
// IPacketListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2012 at 5:11:48 PM
////////

package com.kerious.framework.network;

import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;

public interface IPacketListener {

	void onSendPacketReceived(ReliableConnection authentificatedNetworkPeer, KeriousReliableUDPPacket packet);
	void onSendPacketLost(ReliableConnection authentificatedNetworkPeer, KeriousReliableUDPPacket packet);
	
}
