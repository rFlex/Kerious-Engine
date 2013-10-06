/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.network.protocol.packets
// IKeriousPacketCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 19, 2012 at 4:49:45 PM
////////

package com.kerious.framework.network.protocol.packets;

import com.kerious.framework.network.protocol.KeriousUDPPacket;

public interface IKeriousPacketCreator {

	KeriousUDPPacket packetForIdentifier(byte identifier);
	
}
