/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// HostedPlay.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 7:46:57 PM
////////

package net.kerious.engine.networkgame;

import java.net.InetAddress;
import java.net.SocketException;

import me.corsin.javatools.misc.ValueHolder;
import net.kerious.engine.KeriousEngine;
import net.kerious.engine.console.Console;
import net.kerious.engine.console.DoubleConsoleCommand;
import net.kerious.engine.console.IntegerConsoleCommand;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.network.client.ClientPeer;
import net.kerious.engine.network.client.ClientServerDelegate;
import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.InformationPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.player.Player;
import net.kerious.engine.utils.TemporaryUpdatableArray;
import net.kerious.engine.world.GameWorld;
import net.kerious.engine.world.event.EntityCreatedEvent;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventManager;
import net.kerious.engine.world.event.EventManagerListener;
import net.kerious.engine.world.event.Events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class HostedGame extends Game implements ClientServerDelegate, EventManagerListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private IntMap<ClientPeer> peersAsMap;
	final private TemporaryUpdatableArray<ClientPeer> peersAsArray;
	final private ValueHolder<String> refuseConnectionReasonVH;
	private int playerIdSequence;
	private IntegerConsoleCommand maxPlayers;
	private DoubleConsoleCommand updateRate;
	private boolean renderingEnabled;
	private double time;
	private double nextSnapshot;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public HostedGame(KeriousEngine engine, boolean withRendering) throws SocketException {
		this(engine, 0, withRendering);
	}
	
	public HostedGame(KeriousEngine engine, int port, boolean withRendering) throws SocketException {
		super(engine, new Console(), port);
		
		this.peersAsMap = new IntMap<ClientPeer>();
		this.peersAsArray = new TemporaryUpdatableArray<ClientPeer>(ClientPeer.class);
		this.refuseConnectionReasonVH = new ValueHolder<String>();
		
		this.maxPlayers = new IntegerConsoleCommand("maxplayers", 0, Integer.MAX_VALUE);
		this.maxPlayers.setValue(32);
		
		this.updateRate = new DoubleConsoleCommand("net_updaterate", 1.0, Double.MAX_VALUE);
		this.updateRate.setValue(100.);
		
		this.console.registerCommand(this.maxPlayers);
		this.renderingEnabled = withRendering;
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		this.time += deltaTime;
		
		super.update(deltaTime);
		
		this.updateWorld(deltaTime);
		
		GameWorld world = this.getWorld();
		
		boolean worldReady = world != null && world.isLoaded();
		ClientPeer[] peers = this.peersAsArray.begin();
		
		boolean shouldSendUpdate = this.time >= this.nextSnapshot;
		if (shouldSendUpdate) {
			this.nextSnapshot = this.time + 1.0 / this.updateRate.getValue();
		}
		
		for (int i = 0, length = this.peersAsArray.size; i < length; i++) {
			ClientPeer peer = peers[i];
			
			if (!peer.hasExpired()) {
				peer.update(deltaTime);
				
				if (shouldSendUpdate) {
					if (worldReady && peer.isReadyToReceiveSnapshots()) {
						peer.sendSnapshot(world);
					} else {
						peer.sendKeepAlivePacket();
					}
				}
			} else {
				this.removePeer(peer);
			}
		}
		
		this.peersAsArray.end();
	}
	
	final private ClientPeer getPeer(InetAddress address, int port) {
		int hashCode = KeriousProtocolPeer.computeHashCodeForAddress(address, port);

		return this.peersAsMap.get(hashCode);
	}
	
	/**
	 * Create a peer that represents a connection between this client and the server
	 * @param address
	 * @param port
	 * @return
	 */
	protected ClientPeer createPeer(InetAddress address, int port) {
		return new ClientPeer(address, port);
	}
	
	final private ClientPeer addPeer(String name, InetAddress address, int port) {
		int hashCode = KeriousProtocolPeer.computeHashCodeForAddress(address, port);

		ClientPeer peer = this.peersAsMap.get(hashCode);
		
		if (peer == null) {
			this.playerIdSequence++;
			
			peer = this.createPeer(address, port);
			peer.setPlayerId(this.playerIdSequence);
			peer.setProtocol(this.getProtocol());
			peer.setGate(this.getGate());
			peer.setDelegate(this);
			peer.setName(name);
			
			this.peersAsArray.add(peer);
			this.peersAsMap.put(hashCode, peer);
			
			this.console.print(peer + " connected");
			this.addPlayer(peer);
		}
		
		return peer;
	}
	
	final private void removePeer(ClientPeer peer) {
		this.peersAsArray.removeValue(peer, true);
		this.peersAsMap.remove(peer.hashCode());
		
		this.console.print(peer + " disconnected (" + peer.getDisconnectReason() + ")");
		
		GameWorld world = this.getWorld();
		
		if (world != null) {
			world.getPlayerManager().removePlayer(peer.getPlayerId(), peer.getName());
		}
	}
	
	@Override
	public void onReceived(InetAddress address, int port, Object packet) {
		KeriousProtocolPeer peer = this.getPeer(address, port);
		KeriousPacket keriousPacket = (KeriousPacket)packet;
		
		if (peer == null) {
			this.handleReceivedPacketFromUnknownPeer(address, port, keriousPacket);
		} else {
			this.handleReceivedPacketFromKnownPeer(peer, keriousPacket);
		}
		
		keriousPacket.release();		
	}
	
	final private void handleReceivedPacketFromKnownPeer(KeriousProtocolPeer peer, KeriousPacket packet) {
		peer.handlePacketReceived(packet);
	}
	
	final private void handleReceivedPacketFromUnknownPeer(InetAddress address, int port, KeriousPacket packet) {
		if (packet.packetType == KeriousPacket.TypeConnection) {
			ConnectionPacket connectionPacket = (ConnectionPacket)packet;
			
			switch (connectionPacket.connectionRequest) {
			case ConnectionPacket.ConnectionAsk:
				boolean connectionAccepted = false;
				this.refuseConnectionReasonVH.setValue(null);
				connectionAccepted = this.shouldAcceptConnection(address.getHostAddress(), port, this.refuseConnectionReasonVH);
				
				if (connectionAccepted) {
					ClientPeer peer = this.addPeer(connectionPacket.playerName, address, port);
					peer.handlePacketReceived(connectionPacket);
				} else {
					ConnectionPacket responseConnectionPacket = this.protocol.createConnectionPacket(ConnectionPacket.ConnectionInterrupted);
					responseConnectionPacket.reason = this.refuseConnectionReasonVH.value();
					this.gate.send(responseConnectionPacket, address, port);
					responseConnectionPacket.release();
				}
				
				break;
			case ConnectionPacket.ConnectionAccepted:
			case ConnectionPacket.ConnectionInterrupted:
				// Doesn't make sense as the host is not recognized
				break;
			}
		}
	}
	
	@Override
	public void fillWorldInformations(ClientPeer peer, ObjectMap<String, String> informations) {
		this.fillWorldInformations(informations);
	}
	
	@Override
	public void updateWorldWithCommands(ClientPeer peer, float directionAngle, float directionStrength, long actions) {
		GameWorld world = this.getWorldIfReady();
		
		if (world != null) {
			Player player = world.getPlayerManager().getPlayer(peer.getPlayerId());
			if (player != null) {
				player.handleCommand(directionAngle, directionStrength, actions);
			}
		}
	}
	
	@Override
	public void becameReadyToReceiveSnapshots(ClientPeer peer) {
		GameWorld world = this.getWorldIfReady();
		
		if (world != null) {
			for (Entity entity : world.getEntityManager().getEntites()) {
				EntityCreatedEvent entityCreatedEvent = (EntityCreatedEvent)world.getEventManager().createEvent(Events.EntityCreated);
				
				entityCreatedEvent.entityId = entity.getId();
				entityCreatedEvent.entityType = entity.getType();
				
				peer.sendEvent(entityCreatedEvent);
				
				entityCreatedEvent.release();
			}
		}
	}

	@Override
	protected void worldFailedLoad(String reason) {
		this.setWorld(null);
		
		Array<ClientPeer> peers = this.peersAsArray;
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
			
			clientPeer.setReadyToReceiveSnapshots(false);
			InformationPacket serverFailedLoadingPacket = this.protocol.createInformationPacket(InformationPacket.InformationServerFailedLoading, reason);
			clientPeer.send(serverFailedLoadingPacket);
			serverFailedLoadingPacket.release();
		}
	}
	
	@Override
	protected void worldIsReady() {
		super.worldIsReady();
		
		Array<ClientPeer> peers = this.peersAsArray;
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
			
			this.addPlayer(clientPeer);
		}
	}
	
	
	protected boolean shouldAcceptConnection(String ip, int port, ValueHolder<String> outReason) {
		int maxPlayersValue = this.maxPlayers.getValue();
		int currentConnectedPlayers = this.peersAsArray.size;
		
		if (currentConnectedPlayers < maxPlayersValue) {
			return true;
		}
		
		outReason.setValue("Too many players on the server");
		
		return false;
	}
	
	@Override
	public void onEventFired(EventManager eventManager, Event event) {
		Array<ClientPeer> peers = this.peersAsArray;
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
				
			if (clientPeer.isReadyToReceiveSnapshots()) {
				clientPeer.sendEvent(event);
			}
		}	
	}
	
	@Override
	protected GameWorld createWorld(ObjectMap<String, String> informations) {
		GameWorld world = super.createWorld(informations);
		
		world.setHasAuthority(true);
		world.setRenderingEnabled(this.renderingEnabled);
		world.getEventManager().setListener(this);
		
		return world;
	}
	
	final private void addPlayer(ClientPeer client) {
		GameWorld world = this.getWorldIfReady();
		
		if (world != null) {
			world.getPlayerManager().addPlayer(client.getPlayerId(), client.getName());
			
			RequestPacket packet = this.protocol.createRequestPacket(RequestPacket.RequestLoadWorld);
			client.send(packet);
			packet.release();
		}
	}
	
	@Override
	public void loadWorld(ObjectMap<String, String> informations) {
		super.loadWorld(informations);
		
		Array<ClientPeer> peers = this.peersAsArray;
		ClientPeer[] peersArray = peers.items;
		
		for (int i = 0, length = peers.size; i < length; i++) {
			final ClientPeer clientPeer = peersArray[i];
			
			clientPeer.setReadyToReceiveSnapshots(false);
			
			InformationPacket serverIsLoadingPacket = this.protocol.createInformationPacket(InformationPacket.InformationServerIsLoading, null);
			clientPeer.send(serverIsLoadingPacket);
			serverIsLoadingPacket.release();
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
	
	public Array<ClientPeer> getPeers() {
		return this.peersAsArray;
	}
}
