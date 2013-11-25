/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// ClientGame.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 8:21:34 PM
////////

package net.kerious.engine.play;

import java.net.InetAddress;
import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;
import net.kerious.engine.console.Commands;
import net.kerious.engine.console.SimpleCommand;
import net.kerious.engine.console.StringConsoleCommand;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.client.ServerPeer;
import net.kerious.engine.network.protocol.ServerPeerListener;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.player.PlayerManager;
import net.kerious.engine.player.PlayerModel;
import net.kerious.engine.world.World;
import net.kerious.engine.world.event.Event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class ClientGame extends Game implements ServerPeerListener {

	////////////////////////
	// VARIABLES
	////////////////

	private StringConsoleCommand nameCommand;
	private ClientGameListener listener;
	private CommandPacketCreator commandPacketCreator;
	private ServerPeer serverPeer;
	private int myPlayerId;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ClientGame(KeriousEngine engine) throws SocketException {
		super(engine, engine.getConsole(), 0);
		
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
	
	/**
	 * Create a peer that represents a connection between this client and the server
	 * @param address
	 * @param port
	 * @return
	 */
	protected ServerPeer createPeer(InetAddress address, int port) {
		return new ServerPeer(address, port);
	}
	
	public void connect(String ip, int port) {
		this.disconnect();

		try {
			ServerPeer peer = this.createPeer(InetAddress.getByName(ip), port);
			peer.setProtocol(this.getProtocol());
			peer.setName(this.getName());
			peer.setListener(this);
			peer.setGate(this.getGate());
			
			this.serverPeer = peer;
		} catch (Exception e) {
			if (this.listener != null) {
				this.listener.onConnectionFailed(this, ip, port, e.getMessage());
			}
		}
		
		this.console.print("Attempting to connect to " + ip + ":" + port);
	}
	
	public void disconnect() {
		this.disconnect("Disconnected");
	}
	
	public void disconnect(String reason) {
		if (this.serverPeer != null) {
			ConnectionPacket connection = this.protocol.createConnectionPacket(ConnectionPacket.ConnectionInterrupted);
			connection.reason = reason;
			
			this.serverPeer.send(connection);
			this.onDisconnected(this.serverPeer, reason);
		}
	}
	
	final private void destroyPeer() {
		if (this.serverPeer != null) {
			this.serverPeer.setListener(null);
			this.serverPeer = null;
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		if (this.serverPeer != null) {
			if (this.serverPeer.hasExpired()) {
				this.onDisconnected(this.serverPeer, this.serverPeer.getDisconnectReason());
			} else {
				this.serverPeer.update(deltaTime);
			}
			
			KeriousPacket packet = null;
			
			World world = this.getWorldIfReady();
			if (world != null) {
				if (this.commandPacketCreator != null) {
					packet = this.commandPacketCreator.generateCommandPacket(this.protocol);
				}
			}
			
			if (packet == null) {
				packet = this.protocol.createKeepAlivePacket();
			}
			
			this.serverPeer.send(packet);
			
			packet.release();
		}
		
	}
	
	@Override
	public void onConnected(ServerPeer peer, int playerId) {
		this.console.print("Connected to " + peer.getIP() + ":" + peer.getPort() + " (PlayerID:" + playerId + ")");
		this.myPlayerId = playerId;
		
		if (this.listener != null) {
			this.listener.onConnected(this, peer.getIP(), peer.getPort());
		}
	}
	
	@Override
	public void onConnectionFailed(ServerPeer peer, String reason) {
		this.destroyPeer();
		
		this.console.printError("Failed to connect to " + peer.getIP() + ":" + peer.getPort() + "(" + reason + ")");
		if (this.listener != null) {
			this.listener.onConnectionFailed(this, peer.getIP(), peer.getPort(), reason);
		}
	}
	

	@Override
	public void onDisconnected(ServerPeer peer, String reason) {
		this.destroyPeer();
		
		this.console.print("Disconnected from " + peer.getIP() + ":" + peer.getPort() + " (" + reason + ")");
		
		this.myPlayerId = 0;
		// Temporary implementation
		this.setWorld(null);

		if (this.listener != null) {
			this.listener.onDisconnected(this, peer.getIP(), peer.getPort(), reason);
		}
	}
	
	public void sendToServer(KeriousPacket packet) {
		if (this.serverPeer == null) {
			throw new KeriousException("The client is currently not connected to any server");
		}
		
		this.serverPeer.send(packet);
	}
	
	@Override
	public void onReceived(InetAddress address, int port, Object packet) {
		KeriousPacket keriousPacket = (KeriousPacket)packet;
		
		if (this.serverPeer != null) {
			if (this.serverPeer.getAddress().equals(address) && this.serverPeer.getPort() == port) {
				this.serverPeer.handlePacketReceived(keriousPacket);
			}
		}
		
		keriousPacket.release();
	}
	
	@Override
	protected void worldFailedLoad(String reason) {
		super.worldFailedLoad(reason);
		
		this.disconnect("Unable to load needed resources for the world: " + reason);
	}
	
	@Override
	protected void worldIsReady() {
		this.commandPacketCreator = this.getCommandPacketCreator(this, this.getWorld());
		RequestPacket beginReceiveSnapshots = this.protocol.createRequestPacket(RequestPacket.RequestBeginReceiveSnapshots);
		this.sendToServer(beginReceiveSnapshots);
		beginReceiveSnapshots.release();
	}
	
	@Override
	protected World createWorld(ObjectMap<String, String> informations) {
		final World world = super.createWorld(informations);
		
		world.setHasAuthority(false);
		world.setRenderingEnabled(true);
		
		return world;
	}
	
	@Override
	public void onReceivedWorldInformations(ServerPeer peer, ObjectMap<String, String> informations, boolean shouldLoadWorld) {
		if (shouldLoadWorld) {
			this.loadWorld(informations);
			
			if (this.listener != null) {
				this.listener.onGameWorldLoaded(this, this.getWorld());
			}
		}
	}
	
	@Override
	public void onReceivedInformation(ServerPeer peer, String informationType, String information) {
		this.console.processCommand(Commands.RemoteInformation, informationType, information);
	}

	@Override
	public void onReceivedSnapshot(ServerPeer peer, Array<PlayerModel> players, Array<EntityModel> entityModels, Array<Event> events) {
		World world = this.getWorld();
		
		if (world != null && world.isResourcesLoaded()) {
			final Event[] eventsArray = events.items;
			for (int i = 0, length = events.size; i < length; i++) {
				Event event = eventsArray[i];
				world.fireEvent(event);
			}
			
			final PlayerManager playerManager = world.getPlayerManager();
			final PlayerModel[] playersArray = players.items;
			for (int i = 0, length = players.size; i < length; i++) {
				PlayerModel player = playersArray[i];
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
		}
	}
	
	/**
	 * Return the CommandPacketCreator
	 * If null is returned, the client will just send KeepAlivePacket to prevent the disconnection
	 * @param game
	 * @param world
	 * @return
	 */
	protected abstract CommandPacketCreator getCommandPacketCreator(ClientGame game, World world);

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

	public boolean isConnected() {
		return this.serverPeer != null;
	}
}
