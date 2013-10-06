/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// KeriousUDPPacketFactory.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 4:19:59 PM
////////

package com.kerious.framework.network.protocol;

import java.util.HashMap;
import java.util.Map;

import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.protocol.packets.ConnectionInformationPacket;
import com.kerious.framework.network.protocol.packets.ConnectionPacket;
import com.kerious.framework.network.protocol.packets.DiscoverPacket;
import com.kerious.framework.network.protocol.packets.InformationPacket;
import com.kerious.framework.network.protocol.packets.KeepAlivePacket;
import com.kerious.framework.network.protocol.packets.PlayerCommandPacket;
import com.kerious.framework.network.protocol.packets.SnapshotPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class KeriousUDPPacketFactory {

	////////////////////////
	// VARIABLES
	////////////////

	private static KeriousUDPPacketFactory instance;
	private Map<Byte, ObjectCreator<KeriousUDPPacket>> _translators;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	private KeriousUDPPacketFactory() {
		this._translators = new HashMap<Byte, ObjectCreator<KeriousUDPPacket>>();
		
		this.add(ConnectionPacket.byteIdentifier, new ConnectionPacket.Instancier());
		this.add(DiscoverPacket.byteIdentifier, new DiscoverPacket.Instancier());
		this.add(PlayerCommandPacket.byteIdentifier, new PlayerCommandPacket.Instancier());
		this.add(KeepAlivePacket.byteIdentifier, new KeepAlivePacket.Instancier());
		this.add(InformationPacket.byteIdentifier, new InformationPacket.Instancier());
		this.add(ConnectionInformationPacket.byteIdentifier, new ConnectionInformationPacket.Instancier());
		this.add(SnapshotPacket.byteIdentifier, new SnapshotPacket.Instancier());
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void declareSnapshot(ObjectCreator<KeriousUDPPacket> objectCreator) {
		this._translators.put(SnapshotPacket.byteIdentifier, objectCreator);
	}
	
	public void add(byte byteIdentifier, ObjectCreator<KeriousUDPPacket> objectCreator) {
		ObjectCreator<KeriousUDPPacket> othClass = this._translators.get(byteIdentifier);
		
		if (othClass != null) {
			throw new KeriousException("byte " + byteIdentifier + " already used in factory.");
		}
		
		this._translators.put(byteIdentifier, objectCreator);
	}
	
	public KeriousUDPPacket createFromIdentifier(byte packetIdentifier) {
		ObjectCreator<KeriousUDPPacket> objCreator = this._translators.get(packetIdentifier);
		
		return objCreator != null ? objCreator.instanciate() : null;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public static KeriousUDPPacketFactory getInstance() {
		if (instance == null) {
			instance = new KeriousUDPPacketFactory();
		}
		
		return instance;
	}
}
