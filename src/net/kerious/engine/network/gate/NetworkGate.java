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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import me.corsin.javatools.task.MultiThreadedTaskQueue;
import me.corsin.javatools.task.SimpleTask;
import me.corsin.javatools.task.TaskQueue;
import net.kerious.engine.network.peer.NetworkPeer;
import net.kerious.engine.network.protocol.INetworkProtocol;
import net.kerious.engine.network.protocol.VoidProtocol;

public abstract class NetworkGate implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private Map<Integer, NetworkPeer> peers;
	final private TaskQueue queues;
	private INetworkProtocol protocol;
	private boolean closed;
	private TaskQueue callBackTaskQueue;
	private INetworkGateListener listener;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetworkGate() {
		this(null);
	}
	
	public NetworkGate(INetworkProtocol protocol) {
		this.peers = new HashMap<Integer, NetworkPeer>();
		this.queues = new MultiThreadedTaskQueue(2);
		this.setProtocol(protocol);
		
		this.queues.executeAsync(new Runnable() {
			public void run() {
				beginRead();
			}
		});
	}

	////////////////////////
	// METHODS
	////////////////

	protected abstract ReadPacket readNextPacket() throws IOException;
	protected abstract void sendPacket(InputStream inputStream, InetSocketAddress socketAddress) throws IOException;
	
	private void beginRead() {
		while (!this.closed) {
			Exception exception = null;
			Object deserializedPacket = null;
			NetworkPeer peer = null;
			try {
				final ReadPacket packet = this.readNextPacket();
				if (packet == null) {
					throw new NetworkGateException("The NetworkGate did not return a packet");
				}
				
				peer = this.getPeerForAddress(packet.getSocketAddress());
				deserializedPacket = this.getProtocol().deserialize(packet.getInputStream());
				if (deserializedPacket == null) {
					throw new NetworkGateException("The protocol did not deserialize the packet");
				}
				
			} catch (Exception e) {
				exception = e;
			}
			
			if (this.closed) {
				break;
			}
			
			final Exception thrownException = exception;
			final Object object = deserializedPacket;
			final NetworkPeer thePeer = peer;
			
			if (thePeer != null) {
				this.executeOnAskedQueue(new Runnable() {
					public void run() {
						thePeer.signalReceived(object, thrownException);
					}
				});
			}
			this.executeOnAskedQueue(new Runnable() {
				public void run() {
					if (listener != null) {
						if (thrownException == null) {
							listener.onReceived(thePeer, object);
						} else {
							listener.onFailedReceive(thePeer, thrownException);
						}
					}
				}
			});
		}
	}
	
	private void write(final NetworkPeer peer, final Object packet) throws Exception {
		InputStream inputStream = this.getProtocol().serialize(packet);

		if (inputStream != null) {
			this.sendPacket(inputStream, peer.getAddress());
		} else {
			throw new NetworkGateException("The protocol did not serialize the packet");
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
		this.send(packet, InetSocketAddress.createUnresolved(ip, port));
	}
	
	public void send(Object packet, InetSocketAddress remoteAddress) {
		this.send(packet, this.getPeerForAddress(remoteAddress));
	}
	
	public void send(Object packet, NetworkPeer peer) {
		final Object thePacket = packet;
		final NetworkPeer thePeer = peer;
		
		Runnable writeTask = new SimpleTask() {
			protected void perform() throws Throwable {
				write(thePeer, thePacket);
			}
		}.setListener(new SimpleTask.TaskListener() {
			public void onCompleted(Object taskCreator, final SimpleTask task) {
				executeOnAskedQueue(new Runnable() {
					public void run() {
						if (listener != null) {
							if (task.getThrownException() == null) {
								listener.onSent(thePeer, thePacket);
							} else {
								listener.onFailedSend(thePeer, thePacket, (Exception)task.getThrownException());
							}
						}
						thePeer.signalSent(thePacket, (Exception)task.getThrownException());
					}
				});
			}
		});
		
		this.queues.executeAsync(writeTask);
	}
	
	public void sendToAllRegisteredPeer(Object packet) {
		for (NetworkPeer peer : this.peers.values()) {
			this.send(packet, peer);
		}
	}
	
	/**
	 * Unregister the NetworkPeer from the gate. Every new packet that comes from the IP and port represented by this peer
	 * will result in a new NetworkPeer being allocated
	 * @param peer
	 * @return
	 */
	public boolean unregister(NetworkPeer peer) {
		peer.setGate(null);
		return this.peers.remove(peer.hashCode()) != null;
	}
	
	public boolean isRegistered(NetworkPeer peer) {
		return this.peers.containsKey(peer.hashCode());
	}
	
	/**
	 * Register the NetworkPeer to the gate. If another packet comes from the IP and port represented by this peer, this peer object will be reused
	 * instead of allocating a new one
	 * @param peer
	 */
	public void register(NetworkPeer peer) {
		if (!peer.isRegisterable()) {
			throw new NetworkGateException("The NetworkPeer is currently not registerable.");
		}
		this.peers.put(peer.hashCode(), peer);
		peer.setGate(this);
	}
	
	@Override
	public void close() {
		this.closed = true;
		this.queues.close();
	}
	
	public NetworkPeer getPeerForAddress(InetSocketAddress socketAddress) {
		NetworkPeer peer = this.peers.get(NetworkPeer.computeHashCodeForAddress(socketAddress));
		
		if (peer == null) {
			peer = new NetworkPeer(socketAddress, this);
		}
		
		return peer;
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
}
