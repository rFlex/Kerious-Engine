/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.manager
// NetworkManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 5:41:36 PM
////////

package net.kerious.engine.network.client;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.console.IntegerConsoleCommand;
import net.kerious.engine.network.gate.NetworkGateListener;
import net.kerious.engine.network.gate.UDPGate;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.world.World;

public abstract class KeriousProtocolAbstract implements Closeable, TemporaryUpdatable, NetworkGateListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	final protected KeriousEngine engine;
	final protected KeriousProtocol protocol;
	final protected IntegerConsoleCommand timeoutTimeCommand;
	protected UDPGate gate;
	protected World world;
	private boolean closed;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousProtocolAbstract(KeriousEngine engine) throws SocketException {
		this(engine, 0);
	}

	public KeriousProtocolAbstract(KeriousEngine engine, int port) throws SocketException {
		this.engine = engine;
		this.protocol = new KeriousProtocol();
		this.gate = new UDPGate(this.protocol, port);
		this.gate.setListener(this);
		this.gate.start();
		
		engine.addTemporaryUpdatable(this);
		this.timeoutTimeCommand = new IntegerConsoleCommand("protocol_timeout_time", 1, 10);
		this.timeoutTimeCommand.setValue(3);
		
		engine.getConsole().registerCommand(this.timeoutTimeCommand);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		if (this.gate != null) {
			this.gate.update();
		}
	}

	@Override
	public void close() {
		this.setWorld(null);
		if (this.gate != null) {
			this.gate.close();
			this.gate = null;
		}
		this.engine.getConsole().unregisterCommand(this.timeoutTimeCommand);
		this.closed = true;
	}
	
	@Override
	public void onSent(InetAddress address, int port, Object packet) {
		
	}

	@Override
	public void onFailedSend(InetAddress address, int port, Object packet, Exception exception) {
	}

	@Override
	public void onFailedReceive(InetAddress address, int port, Exception exception) {
//		exception.printStackTrace();
	}
	
	protected void worldWillChange(World oldWorld, World newWorld) {
		if (oldWorld != null) {
			this.protocol.setEntityModelCreator(null);
			this.protocol.setEventCreator(null);
		}
		
		if (newWorld != null) {
			this.protocol.setEntityModelCreator(newWorld.getEntityManager());
			this.protocol.setEventCreator(newWorld.getEventFactory());
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KeriousProtocol getProtocol() {
		return this.protocol;
	}
	
	public int getPort() {
		return this.gate != null ? this.gate.getPort() : -1;
	}

	@Override
	public boolean hasExpired() {
		return this.closed;
	}
	
	public KeriousEngine getEngine() {
		return this.engine;
	}
	
	public void setTimeoutTime(int timeoutTime) {
		this.timeoutTimeCommand.setValue(timeoutTime);
	}
	
	public int getTimeoutTime() {
		return this.timeoutTimeCommand.getValue();
	}

	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.worldWillChange(this.world, world);
		this.world = world;
	}
	
	public UDPGate getGate() {
		return this.gate;
	}
}
