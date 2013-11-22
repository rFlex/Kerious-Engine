/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.play
// Play.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 7:51:33 PM
////////

package net.kerious.engine.play;

import java.io.Closeable;
import java.io.IOException;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;
import net.kerious.engine.console.Console;
import net.kerious.engine.console.ConsoleCommand;
import net.kerious.engine.entity.model.EntityModelCreator;
import net.kerious.engine.network.client.AbstractKeriousProtocolService;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.player.PlayerCreator;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.world.World;
import net.kerious.engine.world.event.EventCreator;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public abstract class Game implements TemporaryUpdatable, Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final protected Console console;
	final protected KeriousEngine engine;
	protected AbstractKeriousProtocolService abstractProtocol;
	private World world;
	private boolean closed;
	private boolean worldIsReady;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Game(KeriousEngine engine) {
		this.console = new Console();
		this.engine = engine;
		
		engine.addTemporaryUpdatable(this);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		if (this.abstractProtocol != null) {
			this.abstractProtocol.update(deltaTime);
		}
		
		World world = this.world;
		if (world != null) {
			if (!this.worldIsReady) {
				if (world.isResourcesLoaded()) {
					this.worldIsReady = true;
					this.worldIsReady();
				} else if (world.hasFailedLoadingResources()) {
					this.worldFailedLoad(world.getFailedLoadingResourcesReason());
					this.setWorld(null);
				}
			}
			
			if (this.world != null) {
				this.world.update(deltaTime);
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		this.closed = true;
		
		if (this.abstractProtocol != null) {
			this.abstractProtocol.close();
			this.abstractProtocol = null;
		}
		this.setWorld(null);
	}
	
	protected void worldFailedLoad(String reason) {
		
	}
	
	protected void worldIsReady() {
		
	}
	
	protected void fillWorldInformations(ObjectMap<String, String> informations) {
		if (this.world != null) {
			for (Entry<String, ConsoleCommand> command : this.world.getConsole().getCommands().entries()) {
				if (command.value.isValueCommand()) {
					informations.put(command.key, command.value.getValueAsString());
				}
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
		World world = this.createWorld(informations);
		this.setWorld(world);
		
		world.beginLoadRessources();
	}

	abstract protected World newWorld(ObjectMap<String, String> informations);
	
	protected World createWorld(ObjectMap<String, String> informations) {
		World world = this.newWorld(informations);
		
		if (world == null) {
			throw new KeriousException("The Play must returns a World");
		}
		
		world.setConsole(this.console);
		
		return world;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public boolean hasExpired() {
		return this.closed;
	}

	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		if (this.world != world) {
			if (this.world != null) {
				this.world.close();
			}
			
			this.world = world;
			
			EntityModelCreator entityModelCreator = null;
			EventCreator eventCreator = null;
			PlayerCreator playerCreator = null;
			
			if (world != null) {
				entityModelCreator = world.getEntityManager();
				eventCreator = world.getEventFactory();
				playerCreator = world.getPlayerManager();
			}
			
			KeriousProtocol keriousProtocol = this.abstractProtocol.getProtocol();
			keriousProtocol.setEntityModelCreator(entityModelCreator);
			keriousProtocol.setEventCreator(eventCreator);
			keriousProtocol.setPlayerCreator(playerCreator);			
		}
	}
}
