/////////////////////////////////////////////////
// Project : SCJavaNetwork
// Package : me.corsin.jnetwork.gate
// Gate.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 23, 2013 at 11:18:41 AM
////////

package net.kerious.engine.network.gate;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

import me.corsin.javatools.misc.Pool;
import me.corsin.javatools.misc.SynchronizedPool;
import net.kerious.engine.network.protocol.INetworkProtocol;
import net.kerious.engine.network.protocol.VoidProtocol;

public abstract class NetworkGate implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private Pool<ByteBuffer> bufferPool;
	final private Pool<NetworkTask> networkTaskPool;
	private Queue<NetworkTask> endedNetworkTasks;
	private INetworkProtocol protocol;
	private boolean closed;
	private NetworkGateListener listener;
	private int maxPacketSize;
	private Thread readThread;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetworkGate() {
		this(null);
	}
		
	public NetworkGate(INetworkProtocol protocol) {
		this.bufferPool = new SynchronizedPool<ByteBuffer>() {
			@Override
			protected ByteBuffer instantiate() {
				return ByteBuffer.allocate(getMaxPacketSize());
			}
		};
		this.networkTaskPool = new SynchronizedPool<NetworkTask>() {
			@Override
			protected NetworkTask instantiate() {
				return new NetworkTask(NetworkGate.this);
			}
		};
		this.endedNetworkTasks = new ArrayDeque<NetworkTask>();
		
		this.setProtocol(protocol);
		
		this.readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				beginRead();
			}
		}, this.getClass().getSimpleName());
		this.maxPacketSize = 65536;
	}

	////////////////////////
	// METHODS
	////////////////

	public void start() {
		this.closed = false;
		this.readThread.start();
	}
	
	public void update() {
		NetworkTask networkTask = null;
		while (this.endedNetworkTasks.size() > 0) {
			synchronized (this.endedNetworkTasks) {
				networkTask = this.endedNetworkTasks.poll();
			}

			if (this.listener != null) {
				if (networkTask.thrownException == null) {
					try {
						networkTask.packet = this.protocol.deserialize(networkTask.buffer);
					} catch (Exception e) {
						networkTask.thrownException = e;
					}
				}
				
				if (networkTask.thrownException == null) {
					this.listener.onReceived(networkTask.address, networkTask.port, networkTask.packet);
				} else {
					this.listener.onFailedReceive(networkTask.address, networkTask.port, networkTask.thrownException);
				}
			}
			networkTask.release();
		}
	}
	
	protected abstract void readPacket(NetworkTask outputTask) throws IOException;
	protected abstract void sendPacket(ByteBuffer buffer, InetAddress address, int port) throws IOException;
	
	private void beginRead() {
		while (!this.closed) {
			NetworkTask networkTask = this.networkTaskPool.obtain();
			ByteBuffer byteBuffer = this.bufferPool.obtain();
			networkTask.buffer = byteBuffer;
			
			try {
				this.readPacket(networkTask);
			} catch (Exception e) {
				networkTask.thrownException = e;
			}
			
			if (this.closed) {
				break;
			}
			
			synchronized (this.endedNetworkTasks) {
				this.endedNetworkTasks.add(networkTask);
			}
		}
	}
	
	public void send(Object packet, String ip, int port) {
		try {
			this.send(packet, InetAddress.getByName(ip), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Object packet, InetAddress address, int port) {
		ByteBuffer buffer = this.bufferPool.obtain();
		buffer.rewind();
		buffer.limit(buffer.capacity());
		
		Exception thrownException = null;
		
		try {
			this.protocol.serialize(packet, buffer);
			this.sendPacket(buffer, address, port);
		} catch (Exception e) {
			thrownException = e;
		}
		
		if (listener != null) {
			if (thrownException == null) {
				this.listener.onSent(address, port, packet);
			} else {
				this.listener.onFailedSend(address, port, packet, thrownException);
			}
		}
		this.bufferPool.release(buffer);
	}
	
	@Override
	public void close() {
		this.closed = true;
		if (this.readThread != null) {
			this.readThread = null;
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public INetworkProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(INetworkProtocol protocol) {
		if (protocol == null) {
			protocol = new VoidProtocol();
		}
		
		this.protocol = protocol;
	}

	public boolean isClosed() {
		return closed;
	}
	
	public NetworkGateListener getListener() {
		return listener;
	}

	public void setListener(NetworkGateListener listener) {
		this.listener = listener;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}
	
	public Pool<ByteBuffer> getBufferPool() {
		return this.bufferPool;
	}
}
