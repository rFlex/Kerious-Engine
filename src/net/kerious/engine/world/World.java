/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// World.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:32:23 PM
////////

package net.kerious.engine.world;

import java.io.Closeable;

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
import net.kerious.engine.resource.ResourceBundle;
import net.kerious.engine.resource.ResourceBundleListener;
import net.kerious.engine.resource.ResourceManager;
import net.kerious.engine.skin.SkinManager;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.world.event.EventFactory;

import com.badlogic.gdx.utils.SnapshotArray;

@SuppressWarnings("rawtypes")
public class World extends ViewController implements TemporaryUpdatable, EntityManagerListener, ResourceBundleListener, Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private SnapshotArray<Entity> entities;
	final private EntityManager entityManager;
	final private SkinManager skinManager;
	final private EventFactory eventFactory;
	final private PlayerManager playerManager;
	final private ResourceBundle resourceBundle;
	private Console console;
	private WorldListener listener;
	private boolean addedToEngine;
	private boolean renderingEnabled;
	private boolean hasAuthority;
	private boolean resourcesLoaded;
	private boolean loadingResources;
	private boolean failedLoadingResources;
	private String loadingFailedReason;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public World(KeriousEngine engine) {
		super(engine);
		
		this.entities = new SnapshotArray<Entity>(true, 64, Entity.class);
		this.skinManager = new SkinManager();
		this.entityManager = new EntityManager();
		this.eventFactory = new EventFactory();
		this.playerManager = this.createPlayerManager();
		this.resourceBundle = new ResourceBundle();
		
		this.entityManager.setListener(this);
		this.resourcesLoaded = true;
		this.setRenderingEnabled(true);
		this.setHasAuthority(true);
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
	
	public void beginLoadRessources() {
		if (!this.loadingResources) {
			this.loadingResources = true;
			this.resourcesLoaded = false;
			this.getEngine().getResourceManager().loadAsync(this.resourceBundle, this);
		}
	}
	
	public void unloadResources() {
		if (this.resourcesLoaded || this.loadingResources) {
			this.getEngine().getResourceManager().unload(this.resourceBundle);
			this.resourcesLoaded = false;
		}
	}
	
	@Override
	public void onLoadedItem(ResourceManager manager, ResourceBundle bundle, String fileName) {
		
	}

	@Override
	public void onLoaded(ResourceManager manager, ResourceBundle bundle) {
		this.loadingResources = false;
		this.resourcesLoaded = true;
		this.failedLoadingResources = false;
		this.loadingFailedReason = null;
	}

	@Override
	public void onLoadingFailed(ResourceManager manager, ResourceBundle bundle, Throwable exception) {
		this.failedLoadingResources = true;
		this.loadingFailedReason = exception.getMessage();
	}

	@Override
	public void onLoadingProgressChanged(ResourceManager manager, ResourceBundle bundle, float progressRatio) {
		
	}
	

	/**
	 * Closes every resources held by the world
	 */
	@Override
	public void close() {
		this.detachView();
		this.unloadResources();
	}
	
	/**
	 * This method is called on the client to generate a command packet that portrays the inputs
	 * If not null, the client will send it otherwise it will send a keep alive instead
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
	
	public void setHasAuthority(boolean value) {
		this.hasAuthority = value;
		this.skinManager.setAutoAttributeId(value);
	}
	
	public Console getConsole() {
		return this.console;
	}
	
	public void setConsole(Console console) {
		this.console = console;
	}
	
	public SkinManager getSkinManager() {
		return this.skinManager;
	}

	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}
	
	public void setRenderingEnabled(boolean value) {
		this.renderingEnabled = value;
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
	}
	
	public int getEntitiesCount() {
		return this.entities.size;
	}
	
	public EventFactory getEventFactory() {
		return this.eventFactory;
	}

	public boolean isResourcesLoaded() {
		return resourcesLoaded;
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public boolean hasFailedLoadingResources() {
		return failedLoadingResources;
	}

	public String getFailedLoadingResourcesReason() {
		return loadingFailedReason;
	}

}
