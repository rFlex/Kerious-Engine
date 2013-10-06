/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.network
// NetworkWorker.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 22, 2012 at 8:22:07 PM
////////

package com.kerious.framework.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.EventListenerHolder;

public class NetworkGate implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////

	private final static int bufferSize = 65536;
	public final EventListenerHolder<NetworkGate> onDisconnected = new EventListenerHolder<NetworkGate>(); 
	public final EventListenerHolder<Packet> onPacketArrived = new EventListenerHolder<Packet>();
	final private NetworkManager nm; 
	private DatagramSocket socket;
	private LinkedList<NetworkEvent> toSendPackets;
	private LinkedList<NetworkEvent> pendingEvents;
	private Thread writeThread;
	private Thread readThread;
	private int workerNum;
	private boolean stopped;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetworkGate(DatagramSocket socket, int workerNum, NetworkManager nm) {
		this.nm = nm;
		this.socket = socket;
		this.toSendPackets = new LinkedList<NetworkEvent>();
		this.pendingEvents = new LinkedList<NetworkEvent>();
		this.stopped = true;
		this.workerNum = workerNum;
	}

	////////////////////////
	// METHODS
	////////////////

	/**
	 * Start the NetworkWorker. Must not be called if the worker is currently running
	 */
	public final void start() {
		if (!this.stopped) {
			throw new RuntimeException("NetworkWorker should be stopped before trying to start it again");
		}
		
		this.stopped = false;
		
		this.readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				NetworkGate.this.startReading();
			}
		}, "Kerious Read NGate " + this.workerNum);
		this.writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				NetworkGate.this.startWriting();
			}
		}, "Kerious Write NGate " + this.workerNum);
		
		this.readThread.start();
		this.writeThread.start();
	}
	
	/**
	 * Stop the NetworkWorker 
	 */
	public final void stop() {
		this.stopped = true;
		
		if (this.readThread != null) {
			try {
				this.readThread.join();
			} catch (InterruptedException e) {
				
			}
			this.readThread = null;
		}
		
		if (this.writeThread != null) {
			synchronized (this.toSendPackets) {
				if (this.toSendPackets.isEmpty()) {
					this.toSendPackets.notify();
				}
			}
			try {
				this.writeThread.join();
			} catch (InterruptedException e) {
				
			}
			this.writeThread = null;
		}
	}
	
	@Override
	public void dispose() {
		if (!this.socket.isClosed()) {
			try {
				this.socket.close();
			} catch (Exception e) {
			}
		}
		this.stop();
	}
	
	/**
	 * Get the next received packet. Return null otherwise
	 * @return the next Received Packet
	 */
	private final NetworkEvent getNextEvent() {
		NetworkEvent packet = null;
		
		synchronized (this.pendingEvents) {
			if (!this.pendingEvents.isEmpty()) {
				packet = this.pendingEvents.removeFirst();
			}
		}
		
		return packet;
	}
	
	public void flush() {
		boolean cont = true;
		
		while (cont) {
			NetworkEvent packet = this.getNextEvent();
			
			if (packet != null) {
				switch (packet.event) {
				case DATA_RECEIVED:
					this.onPacketArrived.call(this, packet.packet);
					break;
				case DISCONNECTED:
					this.onDisconnected.call(this, this);
					break;
				default:
					break;
				}
			} else {
				cont = false;
			}
		}
	}
	
	public final void send(KeriousUDPPacket packet, String ip, int port) {
		this.send(packet, new InetSocketAddress(ip, port));
	}
	
	public final void send(KeriousUDPPacket packet, NetworkPeer peer) {
		this.send(packet, peer.address);
	}
	
	/**
	 * Send the packet to the peer asynchronously.
	 * @param packet
	 * @param peer
	 */
	public final void send(KeriousUDPPacket packet, InetSocketAddress remoteAddr) {
		synchronized (this.toSendPackets) {
			final boolean waiting = this.toSendPackets.isEmpty();
			
			NetworkPeer peer = NetworkPeer.create(remoteAddr);
			
			this.toSendPackets.addLast(NetworkEvent.sendData(packet, peer));
			
			if (waiting) {
				this.toSendPackets.notify();
			}
		}
		
	}
	
	private NetworkEvent nextWritePacket() {
		NetworkEvent packet = null;
		
		synchronized (this.toSendPackets) {
			// If no packet is available
			if (this.toSendPackets.isEmpty()) {
				try {
					// Wait until one comes or until worker has to stop
					this.toSendPackets.wait();
				} catch (InterruptedException e) {
					
				}
			}
			
			// Should be empty only if the worker has to stop
			if (!this.toSendPackets.isEmpty()) {
				packet = this.toSendPackets.removeFirst();
			}
		}
		
		return packet;
	}
	
	private final void startWriting() {
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		DatagramPacket datagramPacket = new DatagramPacket(buffer.array(), bufferSize);
		
		while (!this.stopped) {
			
			NetworkEvent networkEvent = this.nextWritePacket();
			
			if (networkEvent != null) {
				networkEvent.packet.data.pack(buffer);
				datagramPacket.setLength(buffer.position());
				buffer.position(0);
				
				try {
					datagramPacket.setSocketAddress(networkEvent.packet.sender.address);
					this.socket.send(datagramPacket);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					this.stopped = true;
					if (!this.socket.isClosed()) {
						e.printStackTrace();
					}

				}
			}
		}
	}
	
	private final void startReading() {
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		DatagramPacket packet = new DatagramPacket(buffer.array(), bufferSize);
		
		while (!this.stopped) {
			
			try {
				this.socket.receive(packet);
				buffer.position(0);
				buffer.limit(packet.getLength());
				
				try {
					KeriousUDPPacket keriousPacket = KeriousUDPPacket.fromBuffer(buffer, this.nm.getPacketCreator());
					NetworkPeer sender = NetworkPeer.create((InetSocketAddress)packet.getSocketAddress());
					sender.gate = this;
					
					synchronized (this.pendingEvents) {
						this.pendingEvents.addLast(NetworkEvent.dataReceived(keriousPacket, sender));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				this.stopped = true;
		
				synchronized (this.pendingEvents) {
					this.pendingEvents.addLast(NetworkEvent.disconnected());
				}
				
				if (!this.socket.isClosed()) {
					e.printStackTrace();
				}
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final DatagramSocket getSocket() {
		return socket;
	}
	
	public final int getPort() {
		return this.socket.getLocalPort();
	}

	public final void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}
	
	public final boolean isConnected() {
		return !this.socket.isClosed();
	}

}
