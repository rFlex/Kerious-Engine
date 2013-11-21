/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// World.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:32:23 PM
////////

package net.kerious.engine.world;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.console.Console;
import net.kerious.engine.controllers.ViewController;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.EntityManagerListener;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.player.PlayerManager;
import net.kerious.engine.skin.SkinManager;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.world.event.EventFactory;

import com.badlogic.gdx.utils.SnapshotArray;

@SuppressWarnings("rawtypes")
public class World extends ViewController implements TemporaryUpdatable, EntityManagerListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private SnapshotArray<Entity> entities;
	final private EntityManager entityManager;
	final private SkinManager skinManager;
	final private EventFactory eventFactory;
	final private Console console;
	final private PlayerManager playerManager;
	final private boolean renderingEnabled;
	final private boolean hasAuthority;
	private WorldListener listener;
	private boolean addedToEngine;
	private boolean ready;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public World(KeriousEngine engine, boolean renderingEnabled, boolean hasAuthority) {
		super(engine);
		
		this.renderingEnabled = renderingEnabled;
		this.hasAuthority = hasAuthority;
		
		this.entities = new SnapshotArray<Entity>(true, 64, Entity.class);
		this.skinManager = new SkinManager(hasAuthority);
		this.entityManager = new EntityManager();
		this.eventFactory = new EventFactory();
		this.console = new Console();
		this.playerManager = this.createPlayerManager();
		
		this.entityManager.setListener(this);
		this.ready = true;
	}

	////////////////////////
	// METHODS
	////////////////

	/**
	 * Override this to return your own implementation of the PlayerManager
	 * @return
	 */
	protected PlayerManager createPlayerManager() {
		return new PlayerManager();
	}
	
	/**
	 * Register the world to the engine so it starts to update itself
	 */
	public void beginReceiveUpdates() {
		if (!this.addedToEngine) {
			this.addedToEngine = true;
			this.getEngine().addTemporaryUpdatable(this);
		}
	}
	
	/**
	 * Unregister the world to the engine. The world wont update itself anymore after calling this method 
	 */
	public void endReceiveUpdates() {
		if (this.addedToEngine) {
			this.addedToEngine = false;
			this.getEngine().removeTemporaryUpdatable(this);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		if (this.listener != null) {
			this.listener.willUpdateWorld(this);
		}
		
		Entity[] entities = this.entities.begin();
		for (int i = 0, length = this.entities.size; i < length; i++) {
			Entity entity = entities[i];
			
			if (!entity.hasExpired()) {
				entity.update(deltaTime);
			} else {
				this.entities.removeValue(entity, true);
				entity.setWorld(null);
				entity.removedFromWorld();
			}
		}
		this.entities.end();
		
		if (this.listener != null) {
			this.listener.didUpdateWorld(this);
		}
	}
	
	public Entity createEntity(int entityType) throws EntityException {
		Entity entity = this.entityManager.createEntity(entityType);
		
		this.addEntity(entity);
		
		return entity;
	}
	
	public void addEntity(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity may not be null");
		}
		
		this.entities.add(entity);
		
		entity.setWorld(this);
		entity.addedToWorld();
	}
	
	@Override
	public void onEntityCreated(Entity entity) {
		if (this.listener != null) {
			this.listener.onEntityCreated(this, entity);
		}
	}

	@Override
	public void onEntityDestroyed(int entityId) {
		if (this.listener != null) {
			this.listener.onEntityDestroyed(this, entityId);
		}
	}
	
	public void markAsReady() {
		if (this.listener != null) {
			this.listener.onWorldReady(this);
		}
	}
	
	public void markAsNotReady() {
		this.ready = false;
	}
	
	/**
	 * Ask the world to generate the command packet on the client
	 * If nothing is returned, a keep alive packet will be send instead by the protocol
	 * @param protocol
	 * @return
	 */
	public KeriousPacket generateCommandPacket(KeriousProtocol protocol) {
		return null;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public boolean hasExpired() {
		// It never expires. It needs to be force removed
		return false;
	}
	
	public boolean hasAuthority() {
		return this.hasAuthority;
	}
	
	public Console getConsole() {
		return this.console;
	}
	
	public SkinManager getSkinManager() {
		return this.skinManager;
	}

	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public WorldListener getListener() {
		return listener;
	}
	
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public void setListener(WorldListener listener) {
		this.listener = listener;
		
		if (this.listener != null && this.ready) {
			this.listener.onWorldReady(this);
		}
	}
	
	public int getEntitiesCount() {
		return this.entities.size;
	}
	
	public EventFactory getEventFactory() {
		return this.eventFactory;
	}

	public boolean isReady() {
		return ready;
	}
}
