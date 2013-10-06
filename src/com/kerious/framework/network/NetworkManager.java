/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network
// NetworkManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 11:55:47 PM
////////

package com.kerious.framework.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacketFactory;
import com.kerious.framework.network.protocol.packets.IKeriousPacketCreator;

public class NetworkManager implements Disposable {
	
	////////////////////////
	// VARIABLES
	////////////////

	protected LinkedList<NetworkGate> _gates;
	private IKeriousPacketCreator packetCreator;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public NetworkManager() {
		this._gates = new LinkedList<NetworkGate>();
		this.setPacketCreator(null);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public NetworkGate createGate(DatagramSocket socket) {
		NetworkGate worker = new NetworkGate(socket, this._gates.size() + 1, this);
		worker.start();
		
		this._gates.addLast(worker);
		
		return worker;
	}
	
	public NetworkGate openGate() {
		return this.openGate(0);
	}
	
	public NetworkGate openGate(int port) {
		NetworkGate gate = null;
		DatagramSocket socket = null;

		try {
			if (port == 0) {
				socket = new DatagramSocket();
			} else {
				socket = new DatagramSocket(port);
			}
			
			gate = this.createGate(socket);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return gate;
	}
	
	public NetworkGate openMulticastGate() {
		NetworkGate gate = null;
		MulticastSocket socket = null;

		try {
			socket = new MulticastSocket();
			gate = this.createGate(socket);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gate;
	}
	
	public NetworkGate openMulticastGate(String group, int port) {
		NetworkGate gate = null;
		MulticastSocket socket = null;

		try {
			socket = new MulticastSocket(port);
			socket.joinGroup(InetAddress.getByName(group));
			
			gate = this.createGate(socket);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gate;
	}

	public void flush() {
		Iterator<NetworkGate> it = this._gates.iterator();
		
		while (it.hasNext()) {
			NetworkGate gate = it.next();
			
			gate.flush();
			
			if (!gate.isConnected()) {
				it.remove();
			}
		}
	}
	
	@Override
	public void dispose() {
		for (NetworkGate gate : this._gates) {
			gate.dispose();
		}

		this.flush();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final void setPacketCreator(IKeriousPacketCreator packetCreator) {
		if (packetCreator == null) {
			packetCreator = new IKeriousPacketCreator() {
				
				@Override
				public KeriousUDPPacket packetForIdentifier(byte identifier) {
					return KeriousUDPPacketFactory.getInstance().createFromIdentifier(identifier);
				}
			};
		}
		this.packetCreator = packetCreator;
	}
	
	public final IKeriousPacketCreator getPacketCreator() {
		return this.packetCreator;
	}
}
