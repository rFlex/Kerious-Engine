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

import me.corsin.javatools.misc.SynchronizedPool;
import me.corsin.javatools.task.MultiThreadedTaskQueue;
import me.corsin.javatools.task.SimpleTask;
import me.corsin.javatools.task.TaskQueue;
import net.kerious.engine.network.protocol.INetworkProtocol;
import net.kerious.engine.network.protocol.VoidProtocol;

public abstract class NetworkGate implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private TaskQueue queues;
	final private SynchronizedPool<ByteBuffer> buffers;
	private INetworkProtocol protocol;
	private boolean closed;
	private TaskQueue callBackTaskQueue;
	private INetworkGateListener listener;
	private int maxPacketSize;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetworkGate() {
		this(null);
	}
	
	public NetworkGate(INetworkProtocol protocol) {
		this.queues = new MultiThreadedTaskQueue(2);
		this.buffers = new SynchronizedPool<ByteBuffer>() {
			@Override
			protected ByteBuffer instantiate() {
				return ByteBuffer.allocate(getMaxPacketSize());
			}
		};
		
		this.setProtocol(protocol);
		
		this.queues.executeAsync(new Runnable() {
			public void run() {
				beginRead();
			}
		});
		this.maxPacketSize = 8192;
	}

	////////////////////////
	// METHODS
	////////////////

	public void update() {
		
	}
	
	protected abstract void readNextPacket(ReadPacket outputPacket) throws IOException;
	protected abstract void sendPacket(ByteBuffer buffer, int size, InetAddress address, int port) throws IOException;
	
	private void beginRead() {
		ReadPacket readPacket = new ReadPacket();
		while (!this.closed) {
			Exception exception = null;
			Object deserializedPacket = null;
			try {
				this.readNextPacket(readPacket);
				
				if (readPacket.inputStream != null) {
//					deserializedPacket = this.getProtocol().deserialize(readPacket.inputStream);
					if (deserializedPacket == null) {
						throw new NetworkGateException("The protocol did not deserialize the packet");
					}
				}
				
			} catch (Exception e) {
				exception = e;
			}
			
			if (this.closed) {
				break;
			}
			
			final Exception thrownException = exception;
			final Object object = deserializedPacket;
			final InetAddress inetAddress = readPacket.inetAddress;
			final int port = readPacket.port;

			this.executeOnAskedQueue(new Runnable() {
				public void run() {
					if (listener != null) {
						if (thrownException == null) {
							listener.onReceived(inetAddress, port, object);
						} else {
							listener.onFailedReceive(inetAddress, port, thrownException);
						}
					}
				}
			});
		}
	}

	private void executeOnAskedQueue(Runnable runnable) {
		if (this.callBackTaskQueue != null) {
			this.callBackTaskQueue.executeAsync(runnable);
		} else {
			runnable.run();
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
		final InetAddress finalAddress = address;
		final int finalPort = port;
		final Object thePacket = packet;
		
		Runnable writeTask = new SimpleTask() {
			protected void perform() throws Throwable {
//				write(finalAddress, finalPort, thePacket);
			}
		}.setListener(new SimpleTask.TaskListener() {
			public void onCompleted(Object taskCreator, final SimpleTask task) {
				executeOnAskedQueue(new Runnable() {
					public void run() {
						if (listener != null) {
							if (task.getThrownException() == null) {
								listener.onSent(finalAddress, finalPort, thePacket);
							} else {
								listener.onFailedSend(finalAddress, finalPort, thePacket, (Exception)task.getThrownException());
							}
						}
					}
				});
			}
		});
		
		this.queues.executeAsync(writeTask);
	}
	
//	private void send(ByteBuffer buffer, InetAddress address, int port) {
//		int size = buffer.position();
//		
//	}
//		
	@Override
	public void close() {
		this.closed = true;
		this.queues.close();
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

	public TaskQueue getCallBackTaskQueue() {
		return callBackTaskQueue;
	}

	public void setCallBackTaskQueue(TaskQueue callBackTaskQueue) {
		this.callBackTaskQueue = callBackTaskQueue;
	}

	public INetworkGateListener getListener() {
		return listener;
	}

	public void setListener(INetworkGateListener listener) {
		this.listener = listener;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}
}
