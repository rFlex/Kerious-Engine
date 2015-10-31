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
import net.kerious.engine.console.Commands;
import net.kerious.engine.console.Console;
import net.kerious.engine.controllers.ViewController;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.entity.EntityException;
import net.kerious.engine.entity.EntityManager;
import net.kerious.engine.entity.EntityManagerListener;
import net.kerious.engine.gamecontroller.GameController;
import net.kerious.engine.map.GameMap;
import net.kerious.engine.player.Player;
import net.kerious.engine.player.PlayerManager;
import net.kerious.engine.player.PlayerManagerListener;
import net.kerious.engine.resource.Resource;
import net.kerious.engine.resource.ResourceBundle;
import net.kerious.engine.resource.ResourceLoadListener;
import net.kerious.engine.resource.ResourceManager;
import net.kerious.engine.skin.SkinManager;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventManager;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.SnapshotArray;

public abstract class GameWorld extends ViewController implements 	TemporaryUpdatable, EntityManagerListener,
																	PlayerManagerListener, ResourceLoadListener<ResourceBundle>,
																	Closeable, ContactListener {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private SnapshotArray<Entity> entities;
	final private EntityManager entityManager;
	final private SkinManager skinManager;
	final private EventManager eventManager;
	final private PlayerManager playerManager;
	final private ResourceBundle resourceBundle;
	private GameController gameController;
	private World box2dWorld;
	private Console console;
	private boolean addedToEngine;
	private boolean renderingEnabled;
	private boolean hasAuthority;
	private boolean resourcesLoaded;
	private boolean loadingResources;
	private boolean failedLoadingResources;
	private double time;
	private String loadingFailedReason;
	private float pixelsToMetersRatio;
	private float metersToPixelsRatio;
	private int velocityIterations;
	private int positionIterations;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public GameWorld(KeriousEngine engine) {
		super(engine);
		
		this.entities = new SnapshotArray<Entity>(true, 64, Entity.class);
		this.skinManager = new SkinManager();
		this.entityManager = new EntityManager();
		this.eventManager = new EventManager();
		this.playerManager = new PlayerManager();
		this.resourceBundle = new ResourceBundle();
		this.box2dWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(), true);
		this.box2dWorld.setContactListener(this);
		
		this.entityManager.setListener(this);
		this.playerManager.setListener(this);
		
		this.resourcesLoaded = true;
		
		this.setRenderingEnabled(true);
		this.setHasAuthority(true);

		this.setVelocityIterations(6);
		this.setPositionIterations(2);
		this.setMeterPointsSize(100);
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Register the world to the engine so it starts to update itself
	 * You MUST NOT use this method if the World is created inside a Game object
	 * (like HostedGame and ClientGame)
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
		this.time += deltaTime;
		
		this.playerManager.update(deltaTime);
		
		this.box2dWorld.step(deltaTime, this.velocityIterations, this.positionIterations);
		
		Entity[] entities = this.entities.begin();
		for (int i = 0, length = this.entities.size; i < length; i++) {
			Entity entity = entities[i];
			
			if (!entity.hasExpired()) {
				entity.matchModelWithPhysics();
				entity.update(deltaTime);
				entity.updateView();
			} else {
				this.entities.removeValue(entity, true);
				entity.removePhysicsBody();
				entity.setWorld(null);
			}
		}
		this.entities.end();
	}
	
	/**
	 * Create an Entity on this world.
	 * This Entity will automatically have an EntityModel, will
	 * be added to this World and ready() will be called on it
	 * @param entityType
	 * @return
	 * @throws EntityException
	 */
	public Entity createEntity(int entityType) throws EntityException {
		return this.entityManager.createEntity(entityType);
	}
	
	@Override
	public void onEntityCreated(Entity entity) {
		this.entities.add(entity);
		
		entity.setWorld(this);
		entity.ready();
	}

	@Override
	public void onEntityDestroyed(int entityId) {
		
	}
	
	@Override
	public void onPlayerConnected(Player player) {
		player.setWorld(this);
		player.ready();
	}
	
	@Override
	public void onPlayerDisconnected(Player player, String reason) {
		player.setWorld(null);
	}
	
	/**
	 * Convenience method to fire an event
	 * @param event
	 */
	public void fireEvent(Event event) {
		this.eventManager.fireEvent(event);
	}
	
	/**
	 * Load asynchronously every resources declared in the ResourceBundle
	 * Once the loading is finished, ready() will be called on this world
	 */
	public void load() {
		if (!this.loadingResources) {
			this.loadingResources = true;
			this.resourcesLoaded = false;
			this.getEngine().getResourceManager().loadAsync(this.resourceBundle, this);
		}
	}
	
	/**
	 * Unload every loaded resources declared in the ResourceBundle
	 */
	public void unload() {
		if (this.resourcesLoaded || this.loadingResources) {
			this.getEngine().getResourceManager().unload(this.resourceBundle);
			this.resourcesLoaded = false;
		}
	}
	
	abstract protected void ready();

	@Override
	public void onFinishedCompileDependencies(ResourceManager resourceManager,
			Resource<ResourceBundle> resource) {
		this.console.print("Compiled dependencies (have " + resource.getTotalDependenciesCount() + " files to load)");
	}

	@Override
	public void onStartedLoadingDependency(ResourceManager resourceManager,
			Resource<ResourceBundle> resource, Resource<?> loadingDependency) {
	}

	@Override
	public void onFinishedLoadingDependency(ResourceManager resourceManager,
			Resource<ResourceBundle> resource, Resource<?> loadedDependency) {
		this.console.print("Loaded " + loadedDependency.getResourceDescriptor().getFileName());
	}

	@Override
	public void onLoaded(ResourceManager resourceManager,
			Resource<ResourceBundle> resource) {
		this.console.print("Finished loading");
		
		this.loadingResources = false;
		this.resourcesLoaded = true;
		this.failedLoadingResources = false;
		this.loadingFailedReason = null;
		this.entityManager.registerEventListeners(this.eventManager);
		this.playerManager.registerEventListeners(this.eventManager);
		this.ready();
	}

	@Override
	public void onFailedLoading(ResourceManager resourceManager,
			Resource<ResourceBundle> resource, Throwable exception) {
		this.console.printError("Failed loading: " + exception.getMessage());
		exception.printStackTrace();
		
		if (this.console != null) {
			this.console.processCommand(Commands.PrintError, exception.getMessage());
		}
		
		this.failedLoadingResources = true;
		this.loadingFailedReason = exception.getMessage();		
	}

	/**
	 * Closes every resources held by the world
	 */
	@Override
	public void close() {
		this.detachView();
		this.box2dWorld.dispose();
		this.unload();
	}
	
	/**
	 * Fetch every recognized objects from the map and inject the physics into the world
	 * @param map
	 */
	public void injectPhysicsFromMap(GameMap map) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.fixedRotation = true;
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0;
		fixtureDef.restitution = 0;
		PolygonShape shape = new PolygonShape();
		fixtureDef.shape = shape;
		
		float pixelsToMetersRatio = this.pixelsToMetersRatio;
		
		for (MapLayer mapLayer : map.getTiledMap().getLayers()) {
			for (MapObject mapObject : mapLayer.getObjects()) {
				if (mapObject instanceof RectangleMapObject) {
					RectangleMapObject rectangleMapObject = (RectangleMapObject)mapObject;
					Rectangle rect = rectangleMapObject.getRectangle();
					
					bodyDef.position.x = rect.x * pixelsToMetersRatio;
					bodyDef.position.y = rect.y * pixelsToMetersRatio;
					
					Body body = this.box2dWorld.createBody(bodyDef);
					
					float width = rect.width / 2f * pixelsToMetersRatio;
					float height = rect.height / 2f * pixelsToMetersRatio;
					
					bodyDef.position.x = width;
					bodyDef.position.y = height;
					shape.setAsBox(width, height, bodyDef.position, 0);
					
					body.createFixture(shape, 0);
				}
			}
		}
		fixtureDef.shape = null;
		shape.dispose();
	}
	
	@Override
	public void beginContact(Contact contact) {
        if (1 == 1) { return; }
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Entity firstEntity = (Entity)fixtureA.getBody().getUserData();
		Entity secondEntity = (Entity)fixtureB.getBody().getUserData();
		
		if (firstEntity != null && secondEntity != null) {
			firstEntity.beginContact(secondEntity, fixtureA, fixtureB);
			secondEntity.beginContact(firstEntity, fixtureB, fixtureA);
		}
	}

	@Override
	public void endContact(Contact contact) {
        if (1 == 1) { return; }
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Entity firstEntity = (Entity)fixtureA.getBody().getUserData();
		Entity secondEntity = (Entity)fixtureB.getBody().getUserData();
		
		if (firstEntity != null && secondEntity != null) {
			firstEntity.endContact(secondEntity, fixtureA, fixtureB);
			secondEntity.endContact(firstEntity, fixtureB, fixtureA);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
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
		this.eventManager.setCanGenerateEvent(value);
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

	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public int getEntitiesCount() {
		return this.entities.size;
	}
	
	public EventManager getEventManager() {
		return this.eventManager;
	}

	public boolean isLoaded() {
		return resourcesLoaded;
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public boolean hasFailedLoading() {
		return failedLoadingResources;
	}

	public String getFailedLoadingReason() {
		return loadingFailedReason;
	}
	
	public double getTime() {
		return this.time;
	}
	
	public float getGravityX() {
		return this.box2dWorld.getGravity().x;
	}
	
	public float getGravityY() {
		return this.box2dWorld.getGravity().y;
	}
	
	/**
	 * Set the gravity in the physics world.
	 * @param gravityX
	 * @param gravityY
	 */
	public void setGravityX(float gravityX) {
		Vector2 gravity = this.box2dWorld.getGravity();
		gravity.x = gravityX;
		this.box2dWorld.setGravity(gravity);
	}
	
	/**
	 * Set the gravity in the physics world.
	 * @param gravityX
	 * @param gravityY
	 */
	public void setGravityY(float gravityY) {
		Vector2 gravity = this.box2dWorld.getGravity();
		gravity.y = gravityY;
		this.box2dWorld.setGravity(gravity);
	}
	
	/**
	 * Set the gravity in the physics world.
	 * @param gravityX
	 * @param gravityY
	 */
	public void setGravity(float gravityX, float gravityY) {
		Vector2 gravity = this.box2dWorld.getGravity();
		gravity.x = gravityX;
		gravity.y = gravityY;

		this.box2dWorld.setGravity(gravity);
	}
	
	public com.badlogic.gdx.physics.box2d.World getPhysicsWorld() {
		return this.box2dWorld;
	}

	/**
	 * Define how much pixels is a meter.
	 * A value of 100 means 100 pixels in this world coordinate equals 1 meter.
	 * The default value is 100
	 * @param size
	 */
	public void setMeterPointsSize(float size) {
		this.pixelsToMetersRatio = 1f / size;
		this.metersToPixelsRatio = size;
	}
	
	public float getMeterPointsSize() {
		return this.metersToPixelsRatio;
	}
	
	public float getMetersToPixelsRatio() {
		return this.metersToPixelsRatio;
	}

	public float getPixelsToMetersRatio() {
		return this.pixelsToMetersRatio;
	}

	public int getVelocityIterations() {
		return velocityIterations;
	}

	public void setVelocityIterations(int velocityIterations) {
		this.velocityIterations = velocityIterations;
	}

	public int getPositionIterations() {
		return positionIterations;
	}

	public void setPositionIterations(int positionIterations) {
		this.positionIterations = positionIterations;
	}

	public GameController getGameController() {
		return gameController;
	}

	/**
	 * Set the GameController. If not null, this GameController will
	 * be responsible for creating CommandPackets
	 * @param gameController
	 */
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}
}
