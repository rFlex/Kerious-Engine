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
import java.net.InetSocketAddress;
import java.net.SocketException;

import me.corsin.javatools.io.IOUtils;
import net.kerious.engine.network.protocol.INetworkProtocol;

public class UDPGate extends NetworkGate {

	////////////////////////
	// VARIABLES
	////////////////
	
	private DatagramSocket socket;
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
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected ReadPacket readNextPacket() throws IOException {
		ReadPacket packet = null;
		this.socket.receive(receivePacket);
		
		InetSocketAddress address = new InetSocketAddress(this.receivePacket.getAddress(), this.receivePacket.getPort());
		InputStream stream = new ByteArrayInputStream(this.buffer, 0, this.receivePacket.getLength());
		
		packet = new ReadPacket(address, stream);
		
		return packet;
	}

	@Override
	protected void sendPacket(InputStream inputStream, InetSocketAddress socketAddress) {
		try {
			byte[] data = IOUtils.readStream(inputStream);
			DatagramPacket packet = new DatagramPacket(data, 0, data.length, socketAddress);
			
			this.socket.send(packet);
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
