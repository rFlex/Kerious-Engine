/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.gate
// UDPGate.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:29:06 AM
////////

package net.kerious.engine.network.gate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import me.corsin.javatools.io.IOUtils;
import net.kerious.engine.network.protocol.INetworkProtocol;

public class UDPGate extends NetworkGate {

	////////////////////////
	// VARIABLES
	////////////////
	
	private DatagramSocket socket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private byte[] buffer;

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
		
		this.buffer = new byte[65536];
		this.receivePacket = new DatagramPacket(this.buffer, this.buffer.length);
		this.sendPacket = new DatagramPacket(new byte[1], 1);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void readNextPacket(ReadPacket readPacket) throws IOException {
		this.socket.receive(receivePacket);
		
		InputStream stream = new ByteArrayInputStream(this.buffer, 0, this.receivePacket.getLength());

		readPacket.inputStream = stream;
		readPacket.inetAddress = this.receivePacket.getAddress();
		readPacket.port = this.receivePacket.getPort();
	}

	@Override
	protected void sendPacket(ByteBuffer byteBuffer, int size, InetAddress address, int port) {
		try {
			this.sendPacket.setData(byteBuffer.array(), 0, size);
			this.sendPacket.setAddress(address);
			this.sendPacket.setPort(port);
			
			this.socket.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
