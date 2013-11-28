/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// Play.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 7:51:33 PM
////////

package net.kerious.engine.networkgame;

import java.io.Closeable;
import java.net.SocketException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;
import net.kerious.engine.console.Console;
import net.kerious.engine.console.ConsoleCommand;
import net.kerious.engine.entity.model.EntityModelCreator;
import net.kerious.engine.network.client.AbstractKeriousProtocolService;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.player.PlayerModelCreator;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.world.GameWorld;
import net.kerious.engine.world.event.EventCreator;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public abstract class Game extends AbstractKeriousProtocolService implements TemporaryUpdatable, Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final protected Console console;
	final protected KeriousEngine engine;
	private GameWorld world;
	private boolean closed;
	private boolean worldIsReady;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Game(KeriousEngine engine, Console console, int port) throws SocketException {
		super(port);
		this.console = console;
		this.engine = engine;
		
		engine.addTemporaryUpdatable(this);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	
	protected void updateWorld(float deltaTime) {
		GameWorld world = this.world;
		if (world != null) {
			if (!this.worldIsReady) {
				if (world.isLoaded()) {
					this.worldIsReady = true;
					this.worldIsReady();
				} else if (world.hasFailedLoading()) {
					this.worldFailedLoad(world.getFailedLoadingReason());
					this.setWorld(null);
				}
			}
			
			if (this.world != null) {
				this.world.update(deltaTime);
			}
		}
	}
	
	@Override
	public void close() {
		super.close();
		
		this.setWorld(null);
	}
	
	protected void worldFailedLoad(String reason) {
		
	}
	
	protected void worldIsReady() {
		
	}
	
	protected void fillWorldInformations(ObjectMap<String, String> informations) {
		for (Entry<String, ConsoleCommand> command : this.console.getCommands().entries()) {
			if (command.value.isValueCommand()) {
				informations.put(command.key, command.value.getValueAsString());
			}
		}
	}
	
	public void loadWorld() {
		ObjectMap<String, String> informations = new ObjectMap<String, String>();
		this.fillWorldInformations(informations);
		this.loadWorld(informations);
	}
	
	public void loadWorld(ObjectMap<String, String> informations) {
		this.worldIsReady = false;
		try {
			GameWorld world = this.createWorld(informations);
			this.setWorld(world);
			
			world.load();
		} catch (Exception e) {
			this.worldFailedLoad(e.getMessage());
		}
	}

	/**
	 * Create a new world using the informations map
	 * The information map contains every values hold in the 
	 * game console
	 * @param informations
	 * @return
	 */
	abstract protected GameWorld newWorld(ObjectMap<String, String> informations);
	
	protected GameWorld createWorld(ObjectMap<String, String> informations) {
		GameWorld world = this.newWorld(informations);
		
		if (world == null) {
			throw new KeriousException("The Game didn't returns a world");
		}
		
		world.setConsole(this.console);
		
		return world;
	}
	
	protected GameWorld getWorldIfReady() {
		GameWorld world = this.world;

		if (world != null && this.worldIsReady) {
			return world;
		}
		
		return null;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	final public boolean isWorldReady() {
		return this.worldIsReady;
	}
	
	@Override
	public boolean hasExpired() {
		return this.closed;
	}

	public GameWorld getWorld() {
		return world;
	}
	
	public void setWorld(GameWorld world) {
		if (this.world != world) {
			this.worldIsReady = false;
			if (this.world != null) {
				this.world.close();
			}
			
			this.world = world;
			
			EntityModelCreator entityModelCreator = null;
			EventCreator eventCreator = null;
			PlayerModelCreator playerCreator = null;
			
			if (world != null) {
				entityModelCreator = world.getEntityManager();
				eventCreator = world.getEventManager();
				playerCreator = world.getPlayerManager();
			}
			
			KeriousProtocol keriousProtocol = this.protocol;
			keriousProtocol.setEntityModelCreator(entityModelCreator);
			keriousProtocol.setEventCreator(eventCreator);
			keriousProtocol.setPlayerCreator(playerCreator);			
		}
	}
}
