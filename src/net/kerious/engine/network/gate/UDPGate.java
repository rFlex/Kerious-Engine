/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.gate
// UDPGate.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:29:06 AM
////////

package net.kerious.engine.network.gate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.INetworkProtocol;

public class UDPGate extends NetworkGate {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private DatagramSocket socket;
	final private DatagramPacket sendPacket;
	final private DatagramPacket receivePacket;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	/**
	 * Create a UDP gate that listen on a random available port on this machine
	 * @param protocol The protocol to use in the gate
	 * @throws SocketException
	 */
	public UDPGate(INetworkProtocol protocol) throws SocketException {
		this(protocol, 0);
	}
	
	/**
	 * Create a UDP gate that that listen on port
	 * @param protocol The protocol to use in the gate
	 * @param port
	 * @throws SocketException
	 */
	public UDPGate(INetworkProtocol protocol, int port) throws SocketException {
		super(protocol);
		
		if (port == 0) {
			this.socket = new DatagramSocket();	
		} else {
			this.socket = new DatagramSocket(port);
		}
		
		this.receivePacket = new DatagramPacket(new byte[1], 1);
		this.sendPacket = new DatagramPacket(new byte[1], 1);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void readPacket(NetworkTask outputTask) throws IOException {
		ByteBuffer buffer = outputTask.buffer;
		
		this.receivePacket.setData(buffer.array(), 0, buffer.capacity());
		this.socket.receive(this.receivePacket);
		
		buffer.rewind();
		buffer.limit(this.receivePacket.getLength());

		outputTask.address = this.receivePacket.getAddress();
		outputTask.port = this.receivePacket.getPort();
	}

	@Override
	protected void sendPacket(ByteBuffer buffer, InetAddress address, int port) throws IOException {
		this.sendPacket.setData(buffer.array());
		this.sendPacket.setLength(buffer.position());
		this.sendPacket.setAddress(address);
		this.sendPacket.setPort(port);
		
		this.socket.send(this.sendPacket);
	}
	
	@Override
	public void close() {
		this.socket.close();
		super.close();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public int getPort() {
		return this.socket.getLocalPort();
	}
}
