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
import me.corsin.javatools.misc.ReflectionPool;
import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.entity.model.EntityModelCreator;
import net.kerious.engine.gamecontroller.AnalogPad;
import net.kerious.engine.network.protocol.packet.CommandPacket;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.InformationPacket;
import net.kerious.engine.network.protocol.packet.KeepAlivePacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.network.protocol.packet.SnapshotPacket;
import net.kerious.engine.network.protocol.packet.WorldInformationsPacket;
import net.kerious.engine.player.PlayerModel;
import net.kerious.engine.player.PlayerModelCreator;
import net.kerious.engine.utils.FactoryManager;
import net.kerious.engine.world.WorldObjectDatabase;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventCreator;

import com.badlogic.gdx.utils.Array;

public class KeriousProtocol extends FactoryManager implements INetworkProtocol {

	////////////////////////
	// VARIABLES
	////////////////

	private Pool<AnalogPad> pads;
	private EntityModelCreator entityModelCreator;
	private EventCreator eventCreator;
	private PlayerModelCreator playerCreator;
	private WorldObjectDatabase worldDatabase;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousProtocol() {
		this.pads = new Pool<AnalogPad>() {
			protected AnalogPad instantiate() {
				return new AnalogPad();
			}
		};
		this.initPackets();
	}

	////////////////////////
	// METHODS
	////////////////
	
	private void initPackets() {
		this.registerPacketType(KeriousPacket.TypeSnapshot, SnapshotPacket.class);
		this.registerPacketType(KeriousPacket.TypeConnection, ConnectionPacket.class);
		this.registerPacketType(KeriousPacket.TypeKeepAlive, KeepAlivePacket.class);
		this.registerPacketType(KeriousPacket.TypeRequest, RequestPacket.class);
		this.registerPacketType(KeriousPacket.TypeWorldInformations, WorldInformationsPacket.class);
		this.registerPacketType(KeriousPacket.TypeInformation, InformationPacket.class);
		this.registerPacketType(KeriousPacket.TypeCommand, CommandPacket.class);
	}
	
	public <T extends KeriousPacket> void registerPacketType(byte packetType, Class<T> packetClass) {
		this.registerPacketType(packetType, new ReflectionPool<T>(packetClass));
	}
	
	public <T extends KeriousPacket> void registerPacketType(byte packetType, Pool<T> packetPool) {
		this.registerFactory(packetType, packetPool);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createPacket(int packetType) {
		return (T)this.createObject(packetType);
	}
	
	final public ConnectionPacket createConnectionPacket(byte connectionType) {
		ConnectionPacket connectionPacket = (ConnectionPacket)this.createPacket(KeriousPacket.TypeConnection);
		connectionPacket.connectionRequest = connectionType;
		
		return connectionPacket;
	}
	
	final public KeepAlivePacket createKeepAlivePacket() {
		KeepAlivePacket packet = (KeepAlivePacket)this.createPacket(KeriousPacket.TypeKeepAlive);
		
		return packet;
	}
	
	final public WorldInformationsPacket createWorldInformationsPacket() {
		WorldInformationsPacket packet = (WorldInformationsPacket)this.createPacket(KeriousPacket.TypeWorldInformations);
		
		return packet;
	}

	final public RequestPacket createRequestPacket(byte request) {
		final RequestPacket packet = (RequestPacket)this.createPacket(KeriousPacket.TypeRequest);
		
		packet.request = request;
		
		return packet;
	}
	
	final public SnapshotPacket createSnapshotPacket() {
		return this.createPacket(KeriousPacket.TypeSnapshot);
	}
	
	final public InformationPacket createInformationPacket(byte information, String informationString) {
		InformationPacket packet = (InformationPacket)this.createPacket(KeriousPacket.TypeInformation);
		
		packet.information = information;
		packet.informationString = informationString;
		
		return packet;
	}
	
	final public AnalogPad createAnalogPad() {
		return this.pads.obtain();
	}
	
	final public CommandPacket createCommandPacket(Array<AnalogPad> pads, long actionsBitfield) {
		CommandPacket packet = (CommandPacket)this.createPacket(KeriousPacket.TypeCommand);
		
		AnalogPad[] analogPads = pads.items;
		for (int i = 0, length = pads.size; i < length; i++) {
			AnalogPad analogPad = analogPads[i];
			analogPad.retain();
			packet.analogPads.add(analogPad);
		}
		
		packet.actionsBitfield = actionsBitfield;
		
		return packet;
	}
	
	final public PlayerModel createPlayer() {
		if (this.playerCreator == null) {
			throw new KeriousException("No player creator was set in the KeriousProtocol"); 
		}
		
		return this.playerCreator.createPlayerModel();
	}
	
	final public Event createEvent(byte eventType) {
		if (this.eventCreator == null) {
			throw new KeriousException("No event creator was set in the KeriousProtocol"); 
		}
		
		return this.eventCreator.createEvent(eventType);
	}
	
	final public EntityModel createEntityModel(byte entityType) {
		if (this.entityModelCreator == null) {
			throw new KeriousException("Unable to creaty entity model: No entity model creator was set in the KeriousProtocol");
		}
		
		try {
			return this.entityModelCreator.createEntityModel(entityType);
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
	
	public EntityModelCreator getEntityModelCreator() {
		return entityModelCreator;
	}

	public void setEntityModelCreator(EntityModelCreator entityModelCreator) {
		this.entityModelCreator = entityModelCreator;
	}

	public EventCreator getEventCreator() {
		return eventCreator;
	}

	public void setEventCreator(EventCreator eventCreator) {
		this.eventCreator = eventCreator;
	}

	public PlayerModelCreator getPlayerCreator() {
		return playerCreator;
	}

	public void setPlayerCreator(PlayerModelCreator playerCreator) {
		this.playerCreator = playerCreator;
	}

	public WorldObjectDatabase getWorldDatabase() {
		return worldDatabase;
	}

	public void setWorldDatabase(WorldObjectDatabase worldDatabase) {
		this.worldDatabase = worldDatabase;
	}
}
