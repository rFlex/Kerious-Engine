/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// HostedPlay.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 7:46:57 PM
////////

package net.kerious.engine.play;

import java.net.SocketException;

import me.corsin.javatools.misc.ValueHolder;
import net.kerious.engine.KeriousEngine;
import net.kerious.engine.console.Console;
import net.kerious.engine.console.IntegerConsoleCommand;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.network.client.ClientPeer;
import net.kerious.engine.network.client.ServerService;
import net.kerious.engine.network.client.ServerServiceDelegate;
import net.kerious.engine.network.client.ServerServiceListener;
import net.kerious.engine.network.protocol.packet.InformationPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.player.Player;
import net.kerious.engine.world.World;
import net.kerious.engine.world.event.EntityCreatedEvent;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventManager;
import net.kerious.engine.world.event.EventManagerListener;
import net.kerious.engine.world.event.Events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class HostedGame extends Game implements ServerServiceDelegate, ServerServiceListener, EventManagerListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	private ServerService server;
	private IntegerConsoleCommand maxPlayers;
	private boolean renderingEnabled;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public HostedGame(KeriousEngine engine, boolean withRendering) {
		this(engine, 0, withRendering);
	}
	
	public HostedGame(KeriousEngine engine, int port, boolean withRendering) {
		super(engine, new Console());
		
		try {
			this.server = new ServerService(port);
			this.server.setDelegate(this);
			this.server.setListener(this);
			this.abstractProtocol = this.server;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		this.maxPlayers = new IntegerConsoleCommand("maxplayers", 0, Integer.MAX_VALUE);
		this.maxPlayers.setValue(32);
		
		this.console.registerCommand(this.maxPlayers);
		this.renderingEnabled = withRendering;
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		World world = this.getWorld();
		
		boolean worldReady = world != null && world.isResourcesLoaded();
		
		Array<ClientPeer> peers = this.server.getPeers();
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
				
			if (worldReady && clientPeer.isReadyToReceiveSnapshots()) {
				clientPeer.sendSnapshot(world);
			} else {
				clientPeer.sendKeepAlivePacket();
			}
		}
	}

	@Override
	protected void worldFailedLoad(String reason) {
		this.setWorld(null);
		
		Array<ClientPeer> peers = this.server.getPeers();
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
			
			clientPeer.setReadyToReceiveSnapshots(false);
			InformationPacket serverFailedLoadingPacket = this.server.getProtocol().createInformationPacket(InformationPacket.InformationServerFailedLoading, reason);
			clientPeer.send(serverFailedLoadingPacket);
			serverFailedLoadingPacket.release();
		}
	}
	
	@Override
	protected void worldIsReady() {
		super.worldIsReady();
		
		Array<ClientPeer> peers = this.server.getPeers();
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
			
			this.addPlayer(clientPeer);
		}
	}
	
	
	@Override
	public boolean shouldAcceptConnection(ServerService server, String ip, int port, ValueHolder<String> outReason) {
		int maxPlayersValue = this.maxPlayers.getValue();
		int currentConnectedPlayers = this.server.getPeers().size;
		
		if (currentConnectedPlayers < maxPlayersValue) {
			return true;
		}
		
		outReason.setValue("Too many players on the server");
		
		return false;
	}
	
	@Override
	public void onEventFired(EventManager eventManager, Event event) {
		Array<ClientPeer> peers = this.server.getPeers();
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
				
			if (clientPeer.isReadyToReceiveSnapshots()) {
				clientPeer.sendEvent(event);
			}
		}	
	}
	
	@Override
	protected World createWorld(ObjectMap<String, String> informations) {
		World world = super.createWorld(informations);
		
		world.setHasAuthority(true);
		world.setRenderingEnabled(this.renderingEnabled);
		world.getEventManager().setListener(this);
		
		return world;
	}
	
	@Override
	public void fillWorldInformations(ServerService server,
			ObjectMap<String, String> informations) {
		this.fillWorldInformations(informations);
	}
	
	final private void addPlayer(ClientPeer client) {
		World world = this.getWorld();
		
		if (world != null && world.isResourcesLoaded()) {
			world.getPlayerManager().addPlayer(client.getPlayerId(), client.getName());
			
			RequestPacket packet = this.server.getProtocol().createRequestPacket(RequestPacket.RequestLoadWorld);
			client.send(packet);
			packet.release();
		}
	}
	
	@Override
	public void onPeerConnected(ServerService server, ClientPeer client) {
		this.console.print(client + " connected");
		this.addPlayer(client);
	}

	@Override
	public void onPeerDisconnected(ServerService server,
			ClientPeer client) {
		this.console.print(client + " disconnected (" + client.getDisconnectReason() + ")");
		World world = this.getWorld();
		
		if (world != null) {
			world.getPlayerManager().removePlayer(client.getPlayerId(), client.getName());
		}
	}
	
	@Override
	public void onPeerBecameReadyToReceiveSnapshots(ServerService server, ClientPeer client) {
		World world = this.getWorldIfReady();
		
		if (world != null) {
			for (Entity entity : world.getEntityManager().getEntites()) {
				EntityCreatedEvent entityCreatedEvent = (EntityCreatedEvent)world.getEventManager().createEvent(Events.EntityCreated);
				
				entityCreatedEvent.entityId = entity.getId();
				entityCreatedEvent.entityType = entity.getType();
				
				client.sendEvent(entityCreatedEvent);
				
				entityCreatedEvent.release();
			}
		}
	}

	@Override
	public void loadWorld(ObjectMap<String, String> informations) {
		super.loadWorld(informations);
		
		Array<ClientPeer> peers = this.server.getPeers();
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
			
			clientPeer.setReadyToReceiveSnapshots(false);
			
			InformationPacket serverIsLoadingPacket = this.server.getProtocol().createInformationPacket(InformationPacket.InformationServerIsLoading, null);
			clientPeer.send(serverIsLoadingPacket);
			serverIsLoadingPacket.release();
		}
	}

	@Override
	public void updateWorldWithCommands(ServerService server, int playerId, float directionAngle, float directionStrength, long actions) {
		World world = this.getWorldIfReady();
		
		if (world != null) {
			Player player = world.getPlayerManager().getPlayer(playerId);
			if (player != null) {
				player.handleCommand(directionAngle, directionStrength, actions);
			}
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Console getConsole() {
		return this.console;
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers.setValue(maxPlayers);
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers.getValue();
	}
	
	public int getListenPort() {
		return this.server.getPort();
	}
}
