/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 1:24:20 PM
////////

package net.kerious.engine.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

import me.corsin.javatools.misc.Pool;
import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeepAlivePacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.SnapshotPacket;
import net.kerious.engine.utils.FactoryManager;

public class KeriousProtocol extends FactoryManager implements INetworkProtocol {

	////////////////////////
	// VARIABLES
	////////////////
	
	private EntityManager entityManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocol() {
		this.initPackets();
	}

	////////////////////////
	// METHODS
	////////////////
	
	private void initPackets() {
		this.registerFactory(KeriousPacket.SNAPSHOT_TYPE, new Pool<SnapshotPacket>() {
			protected SnapshotPacket instantiate() {
				return new SnapshotPacket();
			}
		});
		
		this.registerFactory(KeriousPacket.CONNECTION_TYPE, new Pool<ConnectionPacket>() {
			protected ConnectionPacket instantiate() {
				return new ConnectionPacket();
			}
		});
		
		this.registerFactory(KeriousPacket.KEEP_ALIVE_TYPE, new Pool<KeepAlivePacket>() {
			protected KeepAlivePacket instantiate() {
				return new KeepAlivePacket();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createPacket(int packetType) {
		return (T)this.createObject(packetType);
	}
	
	final public ConnectionPacket createConnectionPacket(byte connectionType) {
		ConnectionPacket connectionPacket = (ConnectionPacket)this.createPacket(KeriousPacket.CONNECTION_TYPE);
		connectionPacket.connectionRequest = connectionType;
		
		return connectionPacket;
	}
	
	final public KeepAlivePacket createKeepAlivePacket() {
		KeepAlivePacket packet = (KeepAlivePacket)this.createPacket(KeriousPacket.KEEP_ALIVE_TYPE);
		
		return packet;
	}
	
	final public EntityModel createModel(int entityType) {
		if (this.entityManager == null) {
			throw new KeriousException("Unable to creaty entity model: No entity manager was set in the KeriousProtocol");
		}
		
		try {
			return this.entityManager.createEntityModel(entityType);
		} catch (EntityException e) {
			throw new KeriousException("Unable to create entity model: " + e.getMessage());
		}
	}
	
	@Override
	public Object deserialize(ByteBuffer byteBuffer) throws IOException {
		byte packetType = byteBuffer.get();
		KeriousPacket packet = this.createPacket(packetType);
		
		try {
			packet.deserialize(this, byteBuffer);
		} catch (IOException e) {
			packet.release();
			throw e;
		} catch (RuntimeException e) {
			packet.release();
			throw e;
		}
		
		return packet;
	}

	@Override
	public void serialize(Object object, ByteBuffer byteBuffer) {
		KeriousPacket packet = (KeriousPacket)object;
		
		byteBuffer.put(packet.packetType);
		
		packet.serialize(this, byteBuffer);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Set the EntityManager that is necessary to deserialize packets that use EntityModel
	 * @param entityManager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
