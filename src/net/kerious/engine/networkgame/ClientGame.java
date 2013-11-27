/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// ClientGame.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 8:21:34 PM
////////

package net.kerious.engine.networkgame;

import java.net.InetAddress;
import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;
import net.kerious.engine.console.Commands;
import net.kerious.engine.console.SimpleCommand;
import net.kerious.engine.console.StringConsoleCommand;
import net.kerious.engine.network.client.ServerPeer;
import net.kerious.engine.network.protocol.ServerPeerListener;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.network.protocol.packet.SnapshotPacket;
import net.kerious.engine.world.World;

import com.badlogic.gdx.utils.ObjectMap;

public abstract class ClientGame extends Game implements ServerPeerListener {

	////////////////////////
	// VARIABLES
	////////////////

	private StringConsoleCommand nameCommand;
	private SimpleCommand disconnectCommand;
	private ClientGameListener listener;
	private CommandPacketCreator commandPacketCreator;
	private ServerPeer serverPeer;
	private NetInterpolation netInterpolation;
	private int myPlayerId;
	private double sessionTime;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ClientGame(KeriousEngine engine) throws SocketException {
		super(engine, engine.getConsole(), 0);
		
		this.netInterpolation = new NetInterpolation();
		
		this.nameCommand = new StringConsoleCommand("name");
		this.disconnectCommand = new SimpleCommand("disconnect") {
			@Override
			public void handle(String... parameters) {
				disconnect();
			}
		}; 
		
		this.console.registerCommand(this.disconnectCommand);
		this.console.registerCommand(this.nameCommand);
		this.console.registerCommand(this.netInterpolation.getInterpCommand());
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void close() {
		super.close();
		
		this.console.unregisterCommand(this.nameCommand);
		this.console.unregisterCommand(this.disconnectCommand);
		this.console.unregisterCommand(this.netInterpolation.getInterpCommand());
	}
	
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
		this.sessionTime += deltaTime;
		super.update(deltaTime);
		
		World world = this.getWorldIfReady();
		
		this.netInterpolation.update(this.sessionTime, world);
		
		this.updateWorld(deltaTime);

		if (this.serverPeer != null) {
			if (this.serverPeer.hasExpired()) {
				this.onDisconnected(this.serverPeer, this.serverPeer.getDisconnectReason());
			} else {
				this.serverPeer.update(deltaTime);
			}
			
			KeriousPacket packet = null;
			
			if (world != null && this.commandPacketCreator != null) {
				packet = this.commandPacketCreator.generateCommandPacket(this.protocol);
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
	public void onReceivedSnapshot(ServerPeer peer, SnapshotPacket snapshotPacket) {
		World world = this.getWorldIfReady();
		
		if (world != null) {
			this.netInterpolation.handleSnapshot(snapshotPacket);
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
