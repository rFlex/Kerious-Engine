/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// NetInterpolation.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 25, 2013 at 3:56:24 PM
////////

package net.kerious.engine.networkgame;

import net.kerious.engine.console.DoubleConsoleCommand;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.network.protocol.packet.SnapshotPacket;
import net.kerious.engine.player.PlayerManager;
import net.kerious.engine.player.PlayerModel;
import net.kerious.engine.world.World;
import net.kerious.engine.world.event.Event;

import com.badlogic.gdx.utils.Array;

public class NetInterpolation {

	////////////////////////
	// VARIABLES
	////////////////
	
	private DoubleConsoleCommand interpCommand;
	private SnapshotPacket currentSnapshotPacket;
	private double currentTime;
	private Array<SnapshotPacket> pendingSnapshotPackets;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NetInterpolation() {
		this.interpCommand = new DoubleConsoleCommand("net_interp_delay", 0.0, 1.0);
		this.interpCommand.setValue(0.1);
		this.pendingSnapshotPackets = new Array<SnapshotPacket>(true, 32, SnapshotPacket.class);
	}

	////////////////////////
	// METHODS
	////////////////
	
	final private SnapshotPacket getTopSnapshotPacket() {
		return this.pendingSnapshotPackets.size > 0 ? this.pendingSnapshotPackets.items[0] : null;
	}
	
	public void update(double currentTime, World world) {
		this.currentTime = currentTime;
		
		boolean removedTopsnapshotPacket = true;
		
		while (removedTopsnapshotPacket) {
			removedTopsnapshotPacket = false;
			
			SnapshotPacket topSnapshotPacket = this.getTopSnapshotPacket();
			
			if (topSnapshotPacket != null) {
				if (this.currentTime > topSnapshotPacket.timestamp) {
					this.setCurrentSnapshotPacket(topSnapshotPacket);
					this.pendingSnapshotPackets.removeIndex(0);
					
					if (world != null) {
						this.updateWorldWithSnapshot(world, topSnapshotPacket);
					}
					
					topSnapshotPacket.release();
					removedTopsnapshotPacket = true;
				}
			}
		}
		
		if (world != null) {
			this.updateWorldInterpolated(world);
		}
	}
	
	public void handleSnapshot(SnapshotPacket snapshotPacket) {
		snapshotPacket.timestamp = this.currentTime + this.getDelay();
		snapshotPacket.retain();
		
		this.pendingSnapshotPackets.add(snapshotPacket);
	}
	
	private final void updateWorldInterpolated(World world) {
		SnapshotPacket interpolatedPacket = this.getInterpolatedSnapshotPacket();
		
		if (interpolatedPacket != null) {
			final Array<EntityModel> entityModels = interpolatedPacket.models;
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
	
	private final void updateWorldWithSnapshot(World world, SnapshotPacket snapshotPacket) {
		final Array<PlayerModel> players = snapshotPacket.players;
		final Array<Event> events = snapshotPacket.events;

		final Event[] eventsArray = events.items;
		for (int i = 0, length = events.size; i < length; i++) {
			Event event = eventsArray[i];
			world.fireEvent(event);
		}
		
		final Array<EntityModel> entityModels = snapshotPacket.models;
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

		final PlayerManager playerManager = world.getPlayerManager();
		final PlayerModel[] playersArray = players.items;
		for (int i = 0, length = players.size; i < length; i++) {
			PlayerModel player = playersArray[i];
			playerManager.updatePlayer(player);
		}
	}
	
	public SnapshotPacket getInterpolatedSnapshotPacket() {
		SnapshotPacket snapshotPacket = this.currentSnapshotPacket;
		
		if (snapshotPacket != null) {
			SnapshotPacket topSnapshotPacket = this.getTopSnapshotPacket();
			
			if (topSnapshotPacket != null) {
				float ratio = (float)((this.currentTime - snapshotPacket.timestamp) / (topSnapshotPacket.timestamp - snapshotPacket.timestamp));
				snapshotPacket.timestamp = this.currentTime;
				final Array<EntityModel> entityModels = snapshotPacket.models;
				final Array<EntityModel> nextEntityModels = topSnapshotPacket.models;
				final EntityModel[] entityModelsArray = entityModels.items;
				final EntityModel[] nextEntityModelsArray = nextEntityModels.items;
				
				int entityModelsIndex = 0;
				int nextEntityModelsIndex = 0;
				int entityModelsLength = entityModels.size;
				int nextEntityModelsLength = nextEntityModels.size;
				
				while (entityModelsIndex < entityModelsLength && nextEntityModelsIndex < nextEntityModelsLength) {
					EntityModel entityModel = entityModelsArray[entityModelsIndex];
					EntityModel nextEntityModel = nextEntityModelsArray[nextEntityModelsIndex];
					
					// This entity model is not in the packet anymore
					if (entityModel.id < nextEntityModel.id) {
						entityModelsIndex++;
					} else if (entityModel.id > nextEntityModel.id) {
						nextEntityModelsIndex++;
					} else {
						entityModel.interpolate(nextEntityModel, ratio);
						entityModelsIndex++;
						nextEntityModelsIndex++;
					}
				}
			}
		}
		
		return snapshotPacket;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	final private void setCurrentSnapshotPacket(SnapshotPacket packet) {
		if (this.currentSnapshotPacket != null) {
			this.currentSnapshotPacket.release();
			this.currentSnapshotPacket = null;
		}
		
		this.currentSnapshotPacket = packet;
		
		if (packet != null) {
			packet.retain();
		}
	}
	
	public DoubleConsoleCommand getInterpCommand() {
		return this.interpCommand;
	}
	
	public double getDelay() {
		return this.interpCommand.getValue();
	}
	
	public void setDelay(double delay) {
		this.interpCommand.setValue(delay);
	}
}
