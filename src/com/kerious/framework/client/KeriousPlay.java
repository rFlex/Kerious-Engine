/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.client
// KeriousPlay.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 2, 2012 at 11:33:12 PM
////////

package com.kerious.framework.client;

import java.util.HashMap;
import java.util.Map;

import com.kerious.framework.Application;
import com.kerious.framework.KeriousObjectFactory;
import com.kerious.framework.client.interpolate.Interpolator;
import com.kerious.framework.client.interpolate.LagCompensator;
import com.kerious.framework.events.ChatMessageEvent;
import com.kerious.framework.events.ConsoleInstructionEvent;
import com.kerious.framework.events.EntityRegisterEvent;
import com.kerious.framework.events.EntityUnregisterEvent;
import com.kerious.framework.events.GameEvent;
import com.kerious.framework.events.UserConnectedEvent;
import com.kerious.framework.events.UserDisconnectedEvent;
import com.kerious.framework.events.WorldLoadEvent;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.ReliableConnection;
import com.kerious.framework.network.IPacketListener;
import com.kerious.framework.network.Packet;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.network.protocol.packets.ConnectionPacket;
import com.kerious.framework.network.protocol.packets.EntityState;
import com.kerious.framework.network.protocol.packets.KeriousPacket;
import com.kerious.framework.network.protocol.packets.PlayerCommandPacket;
import com.kerious.framework.network.protocol.packets.SnapshotPacket;
import com.kerious.framework.utils.IEventListener;
import com.kerious.framework.utils.Timer;
import com.kerious.framework.world.GameWorld;
import com.kerious.framework.world.NonDrawableStageGroup;
import com.kerious.framework.world.StageGroup;
import com.kerious.framework.world.entities.Entity;
import com.kerious.framework.world.entities.PlayerData;

public class KeriousPlay extends NonDrawableStageGroup implements IEventListener<Packet>, IPacketListener {

	////////////////////////
	// VARIABLES
	////////////////

	final private Application application;
	final private ConnectionToken connectionToken;
	final private Map<Integer, Boolean> gotEvents;
	private KeriousPlayListener listener;
	private GameWorld world;
	private String nextCommandName;
	private String nextCommandContent;
	private Timer nextCommandPacketTimer;
	private StageGroup hud;
	private PlayersManager playersManager;
	private Interpolator interpolator; 
	private LagCompensator lagCompensator;
	private int uploadRate;
	private boolean lagCompensatorEnabled;
	private boolean connected;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static interface KeriousPlayListener {
		GameWorld createGameWorld(String mapName);
		
		void onDisconnected();
		void willSendCommandPacket(PlayerCommandPacket packet);
		void onSnapshotReceived(SnapshotPacket snapshotPacket);
		void onUnmanagedEventReceived(GameEvent event);
		void onEntitySpawned(Entity entity, PlayerData owner);
		void onEntityDestroyed(Entity entity);
		void onUserDisconnected(PlayerData playerStats);
		void onUserConnected(PlayerData playerStats);
		void onWorldLoaded(GameWorld gameWorld);
		void onMessageReceived(PlayerData playerStats, String message, EMessageType messageType);
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousPlay(KeriousPlayListener keriousPlayerListener, Application application, ConnectionToken connectionToken) {
		super();
		
		this.listener = keriousPlayerListener;
		this.application = application;
		this.connectionToken = connectionToken;
		
		connectionToken.gate.onPacketArrived.addListener(this);
		connectionToken.serverPeer.setPacketListener(this);
		
		this.gotEvents = new HashMap<Integer, Boolean>();
		this.uploadRate = 9999;
		this.nextCommandPacketTimer = new Timer();
		
		this.playersManager = new PlayersManager();
		this.connected = true;
		
		this.interpolator = new Interpolator(this);
		this.lagCompensator = new LagCompensator(this);
		
		this.lagCompensatorEnabled = true;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	public void sendCommand(String commandName, String commandContent) {
		this.nextCommandName = commandName;
		this.nextCommandContent = commandContent;
	}
	
	public void disconnect() {
		this.connectionToken.serverPeer.send(KeriousPacket.Connection.askDisconnect(this.connectionToken.serverPeer));
	}
	
	@Override
	public void act(float delta) {
		if (this.connected && this.nextCommandPacketTimer.hasElapsed()) {
			this.nextCommandPacketTimer.start(1f / (float)this.uploadRate);
			
			final String nextCommand = this.nextCommandName;
			final String nextContent = this.nextCommandContent;
			
			PlayerCommandPacket packet = new PlayerCommandPacket();
			packet.setCommandName(nextCommand);
			packet.setCommandContent(nextContent);
			
			this.listener.willSendCommandPacket(packet);
			
			this.connectionToken.serverPeer.send(packet);
			
			this.nextCommandName = "";
			this.nextCommandContent = "";
		}
		
		this.interpolator.flush();
		
		super.act(delta);
		
		this.lagCompensator.update();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		this.connectionToken.gate.dispose();
	}
	
	@Override
	public void onSendPacketReceived(ReliableConnection authentificatedNetworkPeer, KeriousReliableUDPPacket packet) {
		
	}

	@Override
	public void onSendPacketLost(ReliableConnection authentificatedNetworkPeer, KeriousReliableUDPPacket lostPacket) {
		if (lostPacket.packetType == PlayerCommandPacket.byteIdentifier) {
			final PlayerCommandPacket packet = (PlayerCommandPacket)lostPacket;
			final String commandName = packet.getCommandName();
			final String commandContent = packet.getCommandContent();
			
			if (!commandName.equals("")) {
				PlayerCommandPacket newPacket = new PlayerCommandPacket();
				
				newPacket.setCommandName(commandName);
				newPacket.setCommandContent(commandContent);
				
				// Send the packet again
				this.connectionToken.serverPeer.send(newPacket);
			}
		}
	}
	
	private boolean isFromServer(short ident, short code) {
		return ident == this.connectionToken.serverPeer.getIdentifier() && code == this.connectionToken.serverPeer.getCode();
	}

	@Override
	public void onFired(Object sender, Packet arg) {
	final KeriousUDPPacket receivedPacket = arg.data;
		
		if (receivedPacket.reliable) {
			final KeriousReliableUDPPacket reliablePacket = (KeriousReliableUDPPacket)receivedPacket;
			
			if (this.isFromServer(reliablePacket.getIdent(), reliablePacket.getCode())) {
				this.connectionToken.serverPeer.setNetworkpeer(arg.sender);
				this.connectionToken.serverPeer.addToReceivedPacket(reliablePacket);
				
				if (reliablePacket.packetType == SnapshotPacket.byteIdentifier) {
					final SnapshotPacket snapshot = (SnapshotPacket)reliablePacket;
					
					this.interpolator.handleSnapshot(snapshot);
					this.listener.onSnapshotReceived(snapshot);
				}
			}
		} else if (receivedPacket.packetType == ConnectionPacket.byteIdentifier) {
			ConnectionPacket connectionPacket = (ConnectionPacket)receivedPacket;
			
			if (connectionPacket.getConnectionRequest() == ConnectionPacket.DISCONNECTION_ACCEPTED && this.isFromServer(connectionPacket.getChannel(), connectionPacket.getCode())) {
				this.connected = false;
				this.listener.onDisconnected();
				
				this.connectionToken.gate.dispose();
			}
		}
	}

	public final void handleEntityState(EntityState state, long renderingTime) {
		if (this.world != null) {
			final Entity entity = world.getEntity(state.entityID);
			if (entity != null) {
				this.lagCompensator.updateEntity(entity, state, renderingTime);
			}
		}
	}
	
	public final void handleEvent(GameEvent event) {
		Boolean alreadyGot = this.gotEvents.get(event.getEventID());
		
		if (alreadyGot == null) {
			switch (event.eventType) {
			case EntityRegisterEvent.byteIdentifier:
				this.entityRegisterReceived((EntityRegisterEvent)event);
				break;
			case EntityUnregisterEvent.byteIdentifier:
				this.entityUnregisterReceived((EntityUnregisterEvent)event);
				break;
			case ChatMessageEvent.byteIdentifier:
				this.chatMessageReceived((ChatMessageEvent)event);
				break;
			case WorldLoadEvent.byteIdentifier:
				this.mapChangeReceived((WorldLoadEvent)event);
				break;
			case UserConnectedEvent.byteIdentifier:
				this.userConnectedReceived((UserConnectedEvent)event);
				break;
			case UserDisconnectedEvent.byteIdentifier:
				this.userDisconnectedReceived((UserDisconnectedEvent)event);
				break;
			case ConsoleInstructionEvent.byteIdentifier:
				this.consoleInstructionReceived((ConsoleInstructionEvent)event);
				break;
			default:
				this.listener.onUnmanagedEventReceived(event);
				break;
			}
			this.gotEvents.put(event.getEventID(), true);
		}
	}
	
	////////////////////////
	// GAME EVENTS
	////////////////

	private void consoleInstructionReceived(ConsoleInstructionEvent event) {
		if (this.application.getConsole() != null) {
			this.application.getConsole().enterCommand(event.getConsoleCommand());
		}
	}

	private void userDisconnectedReceived(UserDisconnectedEvent event) {
		PlayerData playerStats = this.playersManager.getPlayerStatsForID(event.getUserID());
		
		if (playerStats != null) {
			this.playersManager.removePlayer(playerStats);
			this.listener.onUserDisconnected(playerStats);
		}
	}

	private void userConnectedReceived(UserConnectedEvent event) {
		final PlayerData playerStats = KeriousObjectFactory.createPlayerData();
	
		this.playersManager.addPlayer(event.getUserID(), event.getUserName());
		this.listener.onUserConnected(playerStats);
	}

	private void mapChangeReceived(WorldLoadEvent event) {
		if (this.world != null) {
			this.world.removeFromSuperStage();
			this.world.dispose();
			this.world = null;
		}
		
		try {
			GameWorld gameWorld = this.listener.createGameWorld(event.getMapName());
			if (gameWorld == null) {
				throw new KeriousException("KeriousPlayListener did not create the game world");
			}
			
			this.world = gameWorld;
			
			this.application.presentGameWorld(this.world);
			
			this.setHud(this.hud);
			
			this.listener.onWorldLoaded(gameWorld);
		} catch (Exception e) {
			this.listener.onMessageReceived(null, "Exception raised while loading map " + event.getMapName() + ": " + e.getMessage(), EMessageType.LOCAL_GAME_WARNING);
		}
		
	}

	private void chatMessageReceived(ChatMessageEvent event) {
		switch (event.getSenderID()) {
		case ChatMessageEvent.SERVER_INFORMATION:
		case ChatMessageEvent.SERVER_WARNING:
			this.listener.onMessageReceived(null, event.getMessage(), EMessageType.SERVER_MESSAGE);
			break;
		default: 
			this.listener.onMessageReceived(this.playersManager.getPlayerStatsForID(event.getSenderID()), event.getMessage(), EMessageType.PLAYER_MESSAGE);
		}
	}

	private void entityUnregisterReceived(EntityUnregisterEvent event) {
		if (this.world != null) {
			Entity entity = this.world.factory.getEntityHandler().getEntityByID(event.getEntityID());
			
			if (entity != null) {
				this.listener.onEntityDestroyed(entity);
				this.world.factory.destroyEntity(entity);
			}
		}
	}

	private void entityRegisterReceived(EntityRegisterEvent event) {
		if (this.world != null) {
			PlayerData playerStats = this.playersManager.getPlayerStatsForID(event.getOwnerUserID());
			Entity entity = this.world.factory.spawnEntity(event.getEntityType(), event.getEntityID(), event.getOwnerID(), playerStats);
			
			if (entity != null) {
				this.listener.onEntitySpawned(entity, playerStats);
			}
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final String getNextCommandName() {
		return nextCommandName;
	}

	public final void setNextCommandName(String nextCommandName) {
		this.nextCommandName = nextCommandName;
	}

	public final String getNextCommandContent() {
		return nextCommandContent;
	}

	public final void setNextCommandContent(String nextCommandContent) {
		this.nextCommandContent = nextCommandContent;
	}

	public final KeriousPlayListener getListener() {
		return listener;
	}

	public final void setListener(KeriousPlayListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener cannot be null");
		}
		
		this.listener = listener;
	}

	public final StageGroup getHud() {
		return hud;
	}

	public final void setHud(StageGroup hud) {
		if (this.hud != null) {
			this.hud.removeFromSuperStage();
		}
		
		this.hud = hud;
		
		if (this.hud != null) {
			if (this.world != null) {
				this.world.addStage(this.hud);
			} else {
				this.addStage(this.hud);
			}
		}
	}

	public final PlayersManager getPlayersManager() {
		return this.playersManager;
	}

	public final boolean isConnected() {
		return connected;
	}

	public final Application getApplication() {
		return application;
	}

	public final Interpolator getInterpolator() {
		return interpolator;
	}

	public final LagCompensator getLagCompensator() {
		return lagCompensator;
	}
	
	public int getPing() {
		return this.connectionToken.serverPeer.getPing();
	}
	
	public final boolean isLagCompensatorEnabled() {
		return lagCompensatorEnabled;
	}

	public final void setLagCompensatorEnabled(boolean lagCompensatorEnabled) {
		this.lagCompensatorEnabled = lagCompensatorEnabled;
		
		if (!lagCompensatorEnabled) {
			this.lagCompensator.removeAll();
		}
	}
	
	public final int getMyPlayerID() {
		return this.connectionToken.playerID;
	}
}
