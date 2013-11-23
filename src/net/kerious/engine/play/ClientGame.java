/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// ClientGame.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 8:21:34 PM
////////

package net.kerious.engine.play;

import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.console.Commands;
import net.kerious.engine.console.SimpleCommand;
import net.kerious.engine.console.StringConsoleCommand;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.client.ClientService;
import net.kerious.engine.network.client.ClientServiceListener;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.player.Player;
import net.kerious.engine.player.PlayerManager;
import net.kerious.engine.world.World;
import net.kerious.engine.world.event.EntityDestroyedEvent;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventListener;
import net.kerious.engine.world.event.EventManager;
import net.kerious.engine.world.event.Events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class ClientGame extends Game implements ClientServiceListener {

	////////////////////////
	// VARIABLES
	////////////////

	private ClientService client;
	private StringConsoleCommand nameCommand;
	private ClientGameListener listener;
	private int myPlayerId;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ClientGame(KeriousEngine engine) {
		super(engine, engine.getConsole());
		
		try {
			this.client = new ClientService();
			this.client.setListener(this);
			this.abstractProtocol = this.client;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		this.nameCommand = new StringConsoleCommand("name");
		
		this.console.registerCommand(new SimpleCommand("disconnect") {
			@Override
			public void handle(String... parameters) {
				disconnect();
			}
		});
		this.console.registerCommand(this.nameCommand);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void connect(String ip, int port) {
		this.client.connectTo(this.getName(), ip, port);
		this.console.print("Attempting to connect to " + ip + ":" + port);
	}
	
	public void disconnect() {
		this.client.disconnect("Disconnected by user");
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		World world = this.getWorld();
		
		if (client.isConnected() && world != null) {
			KeriousPacket packet = null;
			
			if (world.isResourcesLoaded()) {
				packet = world.generateCommandPacket(this.client.getProtocol());
			}
			
			if (packet == null) {
				packet = this.client.getProtocol().createPacket(KeriousPacket.TypeKeepAlive);
			}
			
			client.sendToServer(packet);
		}
	}
	
	@Override
	protected void worldFailedLoad(String reason) {
		super.worldFailedLoad(reason);
		
		client.disconnect("Unable to load needed resources for the world: " + reason);
	}
	
	@Override
	protected void worldIsReady() {
		this.client.sendToServer(this.client.getProtocol().createRequestPacket(RequestPacket.RequestBeginReceiveSnapshots));
	}
	
	@Override
	protected World createWorld(ObjectMap<String, String> informations) {
		final World world = super.createWorld(informations);
		
		world.setHasAuthority(false);
		world.setRenderingEnabled(true);
		
		world.getEventManager().addListener(Events.EntityDestroyed, new EventListener() {
			public void onEventFired(EventManager eventManager, Event event) {
				EntityDestroyedEvent entityDestroyedEvent = (EntityDestroyedEvent)event;
				world.getEntityManager().destroyEntity(entityDestroyedEvent.entityId);
			}
		});
		
		return world;
	}
	
	@Override
	public void onReceivedWorldInformations(ClientService clientServer, ObjectMap<String, String> informations, boolean shouldLoadWorld) {
		if (shouldLoadWorld) {
			this.loadWorld(informations);
			
			if (this.listener != null) {
				this.listener.onGameWorldLoaded(this, this.getWorld());
			}
		}
	}

	@Override
	public void onDisconnected(ClientService clientServer, String ip, int port, String reason) {
		this.console.print("Disconnected from " + ip + ":" + port + " (" + reason + ")");
		
		this.myPlayerId = 0;
		// Temporary implementation
		this.setWorld(null);
		
		if (this.listener != null) {
			this.listener.onDisconnected(this, ip, port, reason);
		}
	}

	@Override
	public void onConnected(ClientService clientServer, String ip, int port, int playerId) {
		this.console.print("Connected to " + ip + ":" + port + " (PlayerID:" + playerId + ")");

		this.myPlayerId = playerId;
		
		if (this.listener != null) {
			this.listener.onConnected(this, ip, port);
		}
	}

	@Override
	public void onConnectionFailed(ClientService clientServer, String ip, int port, String reason) {
		this.console.printError("Failed to connect to " + ip + ":" + port + "(" + reason + ")");
		
		if (this.listener != null) {
			this.listener.onConnectionFailed(this, ip, port, reason);
		}
	}
	
	@Override
	public void onReceivedInformation(ClientService clientService, String informationType, String message) {
		this.console.processCommand(Commands.RemoteInformation, informationType, message);
	}

	@Override
	public void onReceivedSnapshot(ClientService clientService, Array<Player> players, Array<EntityModel> entityModels, Array<Event> events) {
		World world = this.getWorld();
		
		if (world != null && world.isResourcesLoaded()) {
			final PlayerManager playerManager = world.getPlayerManager();
			final Player[] playersArray = players.items;
			for (int i = 0, length = players.size; i < length; i++) {
				Player player = playersArray[i];
				playerManager.updatePlayer(player);
			}
			
			final EntityManager entityManager = world.getEntityManager();
			final EntityModel[] entityModelsArray = entityModels.items;
			for (int i = 0, length = entityModels.size; i < length; i++) {
				EntityModel entityModel = entityModelsArray[i];
				try {
					entityManager.updateEntity(entityModel);
				} catch (EntityException e) {
					e.printStackTrace();
				}
			}
			
			final Event[] eventsArray = events.items;
			for (int i = 0, length = events.size; i < length; i++) {
				Event event = eventsArray[i];
				world.fireEvent(event);
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public String getName() {
		return this.nameCommand.getValue();
	}

	public void setName(String name) {
		this.nameCommand.setValue(name);
	}

	public ClientGameListener getListener() {
		return listener;
	}

	public void setListener(ClientGameListener listener) {
		this.listener = listener;
	}

	public int getMyPlayerId() {
		return myPlayerId;
	}


}
