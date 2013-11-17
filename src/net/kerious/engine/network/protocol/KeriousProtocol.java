/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.protocol
// KeriousProtocol.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 1:24:20 PM
////////

package net.kerious.engine.network.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import me.corsin.javatools.io.IOUtils;
import me.corsin.javatools.misc.Pool;
import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.model.EntityModel;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

@SuppressWarnings("rawtypes")
public class KeriousProtocol implements INetworkProtocol {

	////////////////////////
	// VARIABLES
	////////////////
	
	private EntityManager entityManager;
	private IntMap<Pool> objectPoolsByType;
	private ObjectMap<Class, Pool> objectPoolsByClass;
	private Pool<KeriousPacket> packets;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocol() {
		this.packets = new Pool<KeriousPacket>() {
			@Override
			protected KeriousPacket instantiate() {
				return new KeriousPacket();
			}
		};
	}

	////////////////////////
	// METHODS
	////////////////
	
	@SuppressWarnings("unchecked")
	final private <T> T createPacket(Pool pool) {
		Object packet = pool.obtain();
		
		if (packet == null) {
			throw new KeriousException("No packet was created from the pool");
		}
		
		return (T)packet;
	}
	
	public <T> T createPacket(Class<T> packetClass) {
		Pool pool = this.objectPoolsByClass.get(packetClass);
		
		if (pool == null) {
			throw new KeriousException("Packet class " + packetClass.getSimpleName() + " is not registered");
		}
		
		return this.createPacket(pool);
	}

	public <T> T createPacket(int packetType) {
		Pool pool = this.objectPoolsByType.get(packetType);
		
		if (pool == null) {
			throw new KeriousException("Packet type " + packetType + " is not registered");
		}
		
		return this.createPacket(pool);
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
	public Object deserialize(InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.wrap(IOUtils.readStream(inputStream));
		
		KeriousPacket packet = this.createKeriousPacket();
		packet.deserialize(this, byteBuffer);
		
		return packet;
	}

	@Override
	public InputStream serialize(Object object){
		KeriousPacket packet = (KeriousPacket)object;
		ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
		
		packet.serialize(this, byteBuffer);
		
		return new ByteArrayInputStream(byteBuffer.array(), 0, byteBuffer.position());
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
