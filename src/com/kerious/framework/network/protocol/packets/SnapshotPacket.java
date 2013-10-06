package com.kerious.framework.network.protocol.packets;

import java.util.HashMap;
import java.util.Map;

import com.kerious.framework.KeriousObjectFactory;
import com.kerious.framework.events.GameEvent;
import com.kerious.framework.events.GameEvents;
import com.kerious.framework.network.Compressable;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacketFactory;
import com.kerious.framework.utils.Pool;
import com.kerious.framework.utils.Pool.ObjectCreator;
import com.kerious.framework.world.entities.PlayerData;

public class SnapshotPacket extends KeriousReliableUDPPacket implements Compressable<SnapshotPacket> {
	
	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x15;
	protected static Pool<SnapshotPacket> pool = new Pool<SnapshotPacket>();
	final private ObjectCreator<EntityState> _entityStateCreator;
	final private ObjectCreator<PlayerData> _userCreator;
	final private Map<Integer, EntityState> _sortedEntityStates;
	final private Map<Integer, PlayerData> _sortedPlayerInformations;
	protected Pool<SnapshotPacket> currentPool;
	private GameEvent[] _events;
	private EntityState[] _entityStates;
	private PlayerData[] _players;
	private int serverTime;

	////////////////////////
	// NESTED CLASSES
	////////////////

	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new SnapshotPacket();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public SnapshotPacket() {
		super(byteIdentifier);
		
		this._entityStateCreator = KeriousObjectFactory.getEntityStateCreator();
		this._userCreator = KeriousObjectFactory.getPlayerDataCreator();
		
		this._sortedEntityStates = new HashMap<Integer, EntityState>();
		this._sortedPlayerInformations = new HashMap<Integer, PlayerData>();
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void reset() {
		super.reset();
		
		this._events = null;
		this._entityStates = null;
		this._players = null;
		
		this._sortedPlayerInformations.clear();
		this._sortedEntityStates.clear();
	}
	
	@Override
	public void compress(SnapshotPacket packet) {
		for (EntityState entityState : this._entityStates) {
			EntityState oldState = packet.getEntityStateForID(entityState.entityID);
			
			if (oldState != null) {
				entityState.compress(oldState);
			}
		}
		for (PlayerData player : this._players) {
			PlayerData delta = packet._sortedPlayerInformations.get(player.getPlayerID());
			
			if (delta != null) {
				player.compress(delta);
			}
		}
	}
	
	@Override
	public void decompress(SnapshotPacket packet) {
		for (EntityState entityState : this._entityStates) {
			EntityState oldState = packet.getEntityStateForID(entityState.entityID);
			
			if (oldState != null) {
				entityState.decompress(oldState);
			}
		}
		for (PlayerData player : this._players) {
			PlayerData delta = packet._sortedPlayerInformations.get(player.getPlayerID());
			
			if (delta != null) {
				player.decompress(delta);
			}
		}
	}
	
	@Override
	protected void childUnpack() {
		super.childUnpack();
		
		this.serverTime = this.read(serverTime);
		
		short size = 0;

		size = read(size);
		
		this._events = new GameEvent[size];
		
		for (short i = 0; i < size; i++) {
			GameEvent gameEvent = GameEvents.retrieveFromBuffer(this);
			
			if (gameEvent != null) {
				this._events[i] = gameEvent;
			} else {
				break;
			}
		}
		
		this._entityStates = this.read(EntityState.class, this._entityStateCreator);
		this._players = this.read(PlayerData.class, this._userCreator);

		// Adding items to a map for a faster access
		for (EntityState state : this._entityStates) {
			this._sortedEntityStates.put(state.entityID, state);
		}
		for (PlayerData user : this._players) {
			this._sortedPlayerInformations.put(user.getPlayerID(), user);
		}
	}

	@Override
	protected void childPack() {
		super.childPack();
		
		write(this.serverTime);
		
		write((short)this._events.length);
		
		for (GameEvent event : this._events) {
			GameEvents.packInBuffer(event, this);
		}
		
		this.write(this._entityStates);
		this.write(this._players);
	}

	public void setEntities(EntityState[] states) {
		this._entityStates = states;
		
		for (EntityState state : states) {
			this._sortedEntityStates.put(state.entityID, state);
		}
	}
	
	public void setPlayers(PlayerData[] users) {
		this._players = users;
		
		for (PlayerData user : users) {
			this._sortedPlayerInformations.put(user.getPlayerID(), user);
		}
	}
	
	public void setEvents(GameEvent[] events) {
		this._events = events;
	}
	
	public void release() {
//		this.reset();
		
		if (this.currentPool != null) {
//			this.currentPool.release(this);
		}
	}
	
	public static SnapshotPacket create() {
		SnapshotPacket packet = null;
		
//		packet = pool.obtain();
		
		if (packet == null) {
			packet = (SnapshotPacket)KeriousUDPPacketFactory.getInstance().createFromIdentifier(SnapshotPacket.byteIdentifier);
		}
		packet.setPool(pool);
		
		return packet;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final EntityState[] getEntityStates() {
		return this._entityStates;
	}
	
	public final GameEvent[] getEvents() {
		return this._events;
	}
	
	public final PlayerData[] getPlayers() {
		return this._players;
	}
	
	public EntityState getEntityStateForID(int entityID) {
		return this._sortedEntityStates.get(entityID);
	}
	
	public final void setPool(Pool<SnapshotPacket> pool) {
		this.currentPool = pool;
	}
	
	public final Pool<SnapshotPacket> getPool() {
		return this.currentPool;
	}

	public final int getServerTime() {
		return serverTime;
	}

	public final void setServerTime(int serverTime) {
		this.serverTime = serverTime;
	}
}
