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

public class KeriousProtocol implements INetworkProtocol {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final byte INFORMATION_TYPE = 1;
	public static final byte CONNECTION_TYPE = 2;
	public static final byte PLAYER_COMMAND_TYPE = 3;
	public static final byte KEEP_ALIVE_TYPE = 4;
	public static final byte SNAPSHOT_TYPE = 10;
	
	private EntityManager entityManager;
	private Pool<SnapshotPacket> snapshotPackets;
	private Pool<ConnectionPacket> connectionPackets;
	private Pool<KeepAlivePacket> keepAlivePackets;
	private Pool<KeriousPacket> packets;

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
		this.packets = new Pool<KeriousPacket>() {
			protected KeriousPacket instantiate() {
				return new KeriousPacket();
			}
		};
		
		this.snapshotPackets = new Pool<SnapshotPacket>() {
			protected SnapshotPacket instantiate() {
				return new SnapshotPacket();
			}
		};
		this.connectionPackets = new Pool<ConnectionPacket>() {
			protected ConnectionPacket instantiate() {
				return new ConnectionPacket();
			}
		};
		this.keepAlivePackets = new Pool<KeepAlivePacket>() {
			protected KeepAlivePacket instantiate() {
				return new KeepAlivePacket();
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createPacket(int packetType) {
		
		switch (packetType) {
		case CONNECTION_TYPE:
			return (T)this.connectionPackets.obtain();
		case SNAPSHOT_TYPE:
			return (T)this.snapshotPackets.obtain();
		case KEEP_ALIVE_TYPE:
			return (T)this.keepAlivePackets.obtain();
		}
		
		throw new KeriousException("Packet type " + packetType + " is not recognized");
	}
	
	final public KeriousPacket createConnectionPacket(byte connectionType) {
		KeriousPacket packet = this.createKeriousPacket();
		ConnectionPacket connectionPacket = this.connectionPackets.obtain();
		
		packet.packetType = CONNECTION_TYPE;
		packet.childPacket = connectionPacket;
		connectionPacket.type = connectionType;
		
		return packet;
	}
	
	final public KeriousPacket createKeepAlivePacket() {
		KeriousPacket packet = this.createKeriousPacket();
		
		packet.packetType = KEEP_ALIVE_TYPE;
		packet.childPacket = this.keepAlivePackets.obtain();
		
		return packet;
	}
	
	final public KeriousPacket createKeriousPacket(byte subPacketType) {
		KeriousSerializableData<?> subPacket = this.createPacket(subPacketType);
		KeriousPacket packet = this.createKeriousPacket();
		
		packet.packetType = subPacketType;
		packet.childPacket = subPacket;
		
		return packet;
	}
	
	final public KeriousPacket createKeriousPacket() {
		return this.packets.obtain();
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
		KeriousPacket packet = this.createKeriousPacket();
		
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
		packet.serialize(this, byteBuffer);
	}
	
//	@Override
//	public Object deserialize(InputStream inputStream) throws IOException {
//		ByteBuffer byteBuffer = ByteBuffer.wrap(IOUtils.readStream(inputStream));
//		
//		KeriousPacket packet = this.createKeriousPacket();
//		packet.deserialize(this, byteBuffer);
//		
//		return packet;
//	}
//
//	@Override
//	public InputStream serialize(Object object){
//		KeriousPacket packet = (KeriousPacket)object;
//		ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
//		
//		packet.serialize(this, byteBuffer);
//		
//		return new ByteArrayInputStream(byteBuffer.array(), 0, byteBuffer.position());
//	}

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
