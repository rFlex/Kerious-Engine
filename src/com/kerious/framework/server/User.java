/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.world.network
// User.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 24, 2012 at 2:57:17 PM
////////

package com.kerious.framework.server;

import java.util.ArrayList;
import java.util.LinkedList;

import com.kerious.framework.KeriousObjectFactory;
import com.kerious.framework.events.EntityRegisterEvent;
import com.kerious.framework.events.EntityUnregisterEvent;
import com.kerious.framework.events.GameEvent;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.ReliableConnection;
import com.kerious.framework.network.IPacketListener;
import com.kerious.framework.network.NetworkPeer;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.packets.EntityState;
import com.kerious.framework.network.protocol.packets.SnapshotPacket;
import com.kerious.framework.utils.IEventListener;
import com.kerious.framework.utils.Pool.ObjectCreator;
import com.kerious.framework.utils.StopWatch;
import com.kerious.framework.utils.Timer;
import com.kerious.framework.world.GameWorld;
import com.kerious.framework.world.entities.PlayerData;
import com.kerious.framework.world.entities.Entity;

public class User implements IPacketListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final float TIME_OUT = 10;
	private PlayerData playerData;
	private UserListener listener;
	private KeriousServer server;
	private ReliableConnection connection;
	private ArrayList<Entity> entities;
	private LinkedList<GameEvent> pendingEvents;
	private SnapshotPacket lastReceivedSnapshot;
	private StopWatch readyToSnapshotTimer;
	private Timer timedOut;
	private GameWorld gameWorld;
	private float uploadRate;
	private int currentEventIdentifierSequence;
	private EntityRegistered entityRegisteredFunction;
	private EntityUnregistered entityUnregisterFunction;

	////////////////////////
	// NESTED CLASSES
	////////////////
	
	private class EntityRegistered implements IEventListener<Entity> {

		@Override
		public void onFired(Object sender, Entity arg) {
			if (arg.isReplicated()) {
				entities.add(arg);
				sendEvent(EntityRegisterEvent.create(arg));
			}
		}
		
	}
	
	private class EntityUnregistered implements IEventListener<Entity> {

		@Override
		public void onFired(Object sender, Entity arg) {
			if (arg.isReplicated()) {
				entities.remove(arg);
				sendEvent(EntityUnregisterEvent.create(arg));
			}
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public User(KeriousServer server, int userID) {
		this.server = server;
		this.entities = new ArrayList<Entity>();
		this.pendingEvents = new LinkedList<GameEvent>();
		
		this.readyToSnapshotTimer = new StopWatch();
		this.timedOut = new Timer();
		this.timedOut.start(TIME_OUT);
		this.uploadRate = 100;
		this.currentEventIdentifierSequence = 10;
		
		this.entityRegisteredFunction = new EntityRegistered();
		this.entityUnregisterFunction = new EntityUnregistered();
		
		this.playerData = KeriousObjectFactory.createPlayerData();
		this.playerData.setUser(this);
		this.playerData.setPlayerID(userID);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void disconnect() {
		this.server.userManager.removeUser(this);
	}
	
	public void replicateWorld(GameWorld world) {
		this.stopReplicatingWorld();
		
		this.gameWorld = world;
		
		this.gameWorld.factory.onEntityRegistered.addListener(this.entityRegisteredFunction);
		this.gameWorld.factory.onEntityUnregistered.addListener(this.entityUnregisterFunction);
		
		for (Entity entity : world.factory.getEntityHandler()) {
			this.entityRegisteredFunction.onFired(this, entity);
		}
	}
	
	public void stopReplicatingWorld() {
		if (this.gameWorld != null) {
			this.gameWorld.factory.onEntityRegistered.removeListener(this.entityRegisteredFunction);
			this.gameWorld.factory.onEntityUnregistered.removeListener(this.entityUnregisterFunction);
			
			this.gameWorld = null;
		}
	}
	
	public void sendEvent(GameEvent event) {
		if (this.connection != null && this.connection.isConnected()) {
			event.setEventID(this.currentEventIdentifierSequence);
			this.pendingEvents.addLast(event);
			
			this.currentEventIdentifierSequence++;
		}
	}
	
	public void handlePacket(NetworkPeer basicPeer, KeriousReliableUDPPacket packet) {
		if (this.connection != null) {
			this.connection.setNetworkpeer(basicPeer);
			this.connection.addToReceivedPacket(packet);
		}
		
		if (this.listener != null) {
			this.listener.onPacketReceived(packet);
		}
	}
	
	public void sendTick() {
		if (this.connection == null) {
			throw new KeriousException("The user has not any peer attached");
		}
		
		SnapshotPacket packet = SnapshotPacket.create();
				
		packet.setServerTime(this.server.getCurrentTime());
		
		EntityState[] states = new EntityState[this.entities.size()];
		
		final ObjectCreator<EntityState> creator = KeriousObjectFactory.getEntityStateCreator();
		
		for (int i = 0; i < this.entities.size(); i++) {
			final EntityState state = creator.instanciate();
			state.fillFromEntity(this.entities.get(i));
			states[i] = state;
		}
		
		packet.setEntities(states);
		GameEvent[] events = new GameEvent[this.pendingEvents.size()];
		packet.setEvents(this.pendingEvents.toArray(events));
		
		PlayerData[] players = new PlayerData[this.server.userManager.getConnectedUsers()];
		for (int i = 0; i < players.length; i++) {
			players[i] = this.server.userManager.getUserForPosition(i).getPlayerData().snapshot();
		}
		packet.setPlayers(players);
		
		if (this.listener != null) {
			this.listener.willSendSnapshot(packet);
		}
		
		if (this.lastReceivedSnapshot != null) {
			packet.compress(lastReceivedSnapshot);
		}
		
		this.connection.send(packet);
		
		this.readyToSnapshotTimer.start();
	}

	@Override
	public void onSendPacketReceived(ReliableConnection authentificatedNetworkPeer, KeriousReliableUDPPacket packet) {
		this.timedOut.start(TIME_OUT);
		
		if (packet.packetType == SnapshotPacket.byteIdentifier) {
			SnapshotPacket snapshot = (SnapshotPacket)packet;
			
			for (GameEvent event : snapshot.getEvents()) {
				this.pendingEvents.remove(event);
			}
			
			if (this.lastReceivedSnapshot != null) {
				this.lastReceivedSnapshot.release();
			}
			
			this.lastReceivedSnapshot = snapshot;
		}
	}

	@Override
	public void onSendPacketLost(ReliableConnection authentificatedNetworkPeer, KeriousReliableUDPPacket packet) {
		if (packet.packetType == SnapshotPacket.byteIdentifier) {
			((SnapshotPacket)packet).release();
		}
	}
	
	public final void updateLogic() {
		if (this.listener != null) {
			this.listener.updateLogic();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public void setConnection(ReliableConnection connection) {
		if (this.connection != null) {
			this.connection.setPacketListener(null);
			this.connection = null;
		}
		
		this.connection = connection;
		
		if (connection != null) {
			connection.setPacketListener(this);
		}
	}
	
	public final ReliableConnection getConnection() {
		return this.connection;
	}
	
	public final KeriousServer getServer() {
		return this.server;
	}
	
	public final void setUploadRate(float uploadRate) {
		this.uploadRate = uploadRate;
	}
	
	public final float getUploadRate() {
		return this.uploadRate;
	}
	
	public final boolean isReadyForSnapshot() {
		return this.connection.isConnected() && this.readyToSnapshotTimer.secondCurrent() > (1f / this.uploadRate);
	}

	public final PlayerData getPlayerData() {
		return this.playerData;
	}
	
	public final void setPlayerData(PlayerData playerData) {
		if (playerData == null) {
			throw new KeriousException("Cannot set a NULL PlayerData");
		}
		
		this.playerData = playerData;
	}
	
	public final void setListener(UserListener listener) {
		this.listener = listener;
	}
	
	public final UserListener getListener() {
		return this.listener;
	}

	public boolean hasTimedOut() {
		return this.connection.isConnected() && this.timedOut.hasElapsed();
	}
	
}
