/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// WorldReplicator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 8:14:36 PM
////////

package net.kerious.engine.world;

import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.network.gate.NetworkGate;
import net.kerious.engine.network.gate.UDPGate;
import net.kerious.engine.network.protocol.KeriousPeer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

@SuppressWarnings("rawtypes") 
public class WorldReplicator implements Disposable, WorldListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	private NetworkGate gate;
	final private Array<KeriousPeer> peers;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public WorldReplicator(KeriousEngine engine) {
		this(engine, 0);
	}

	public WorldReplicator(KeriousEngine engine, int port) {
		if (engine == null) {
			throw new IllegalArgumentException("engine may not be null");
		}
		
		try {
			this.gate = new UDPGate(null, port);
			this.gate.setCallBackTaskQueue(engine.getTaskQueue());
		} catch (SocketException e) {
			engine.getConsole().print("ERROR: Unable to create gate for WorldReplicator: " + e.getMessage());
		}
		
		this.peers = new Array<KeriousPeer>(false, 32, KeriousPeer.class);
	}

	////////////////////////
	// METHODS
	////////////////

	public void addPeer(KeriousPeer peer) {
		if (peer == null) {
			throw new IllegalArgumentException("peer may not be null");
		}
		this.peers.add(peer);
		
		if (this.gate != null) {
			this.gate.register(peer);
		}
	}
	
	public void removePeer(KeriousPeer peer) {
		this.peers.removeValue(peer, true);
		
		if (this.gate != null) {
			this.gate.unregister(peer);
		}
	}
	
	@Override
	public void willUpdateWorld(World world) {
		
	}

	@Override
	public void didUpdateWorld(World world) {
		Object snapshot = this.generateSnapshot(world);
		this.sendSnapshot(snapshot);
	}
	
	public Object generateSnapshot(World world) {
		return null;
	}
	
	public void sendSnapshot(Object snapshotObject) {
		if (this.gate != null) {
			
		}
	}
	
	@Override
	public void onEntityCreated(World world, Entity entity) {
		
	}

	@Override
	public void onEntityDestroyed(World world, int entityId) {
		
	}
	
	@Override
	public void dispose() {
		if (this.gate != null) {
			this.gate.close();
			this.gate = null;
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
