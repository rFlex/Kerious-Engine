/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.entities
// Entity.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 29 sept. 2012 at 22:24:57
////////

package com.kerious.framework.world.entities;

import com.badlogic.gdx.math.Vector2;
import com.kerious.framework.actions.VelocityAction;
import com.kerious.framework.collisions.CollisionHandler;
import com.kerious.framework.collisions.ICollisionable;
import com.kerious.framework.drawable.AnimatedSpriteActor;
import com.kerious.framework.utils.GeometryUtils;
import com.kerious.framework.world.GameWorld;

public abstract class Entity extends AnimatedSpriteActor implements ICollisionable {

	////////////////////////
	// VARIABLES
	////////////////

	final protected Vector2 viewDirection;
	final protected Vector2 moveDirection;
	final protected VelocityAction velocityAction;
	final protected boolean canBeCollisioned;
	final protected boolean replicated;
	final protected boolean runningOnServer;
	final protected int layer;
	final protected GameWorld gameWorld;
	protected CollisionHandler collisionHandler;
	protected Entity parentEntity;
	protected PlayerData playerData;
	protected boolean velocityEnabled;
	protected boolean registered;
	protected float speed;
	protected float viewAngle;
	protected int entityType;
	protected int entityID;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Entity(GameWorld world, int layer, boolean enableVelocity, boolean graphicsEnabled, boolean collisionnable, boolean replicated) {
		super(world.application, graphicsEnabled);
		
		this.viewDirection = new Vector2();
		this.moveDirection = new Vector2();
		this.canBeCollisioned = collisionnable;
		
		this.layer = layer;
		
		this.velocityAction = new VelocityAction();
		this.addAction(this.velocityAction);
		this.velocityEnabled = enableVelocity;
		
		this.gameWorld = world;
		
		this.runningOnServer = !graphicsEnabled;
		this.replicated = replicated;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Called when the object is recycled from the pool, just before being used. It must reset the object
	 * and prepare it for a brand new use.
	 */
	public void init() {
		this.viewDirection.x = 0;
		this.viewDirection.y = 0;
		this.moveDirection.x = 0;
		this.moveDirection.y = 0;
		
		this.setPosition(0, 0);
		this.setSpeed(1);
	}
	
	/**
	 * Called just after init (or after initGraphicalContext on the client). It must contains the procedures
	 * for adding the object to the world.
	 * If the object has to be added to a collisionhandler, it should be added here.
	 * NOTE: Entity class already adds this entity to the world. It will do so if you call the "super" method.
	 * You can avoid this behavior by removing the "super" method invocation.
	 * @param world
	 * @param parentEntity
	 */
	public void addToWorld(Entity parentEntity, PlayerData playerData) {
		this.gameWorld.addEntity(this, this.layer);
	}
	
	/**
	 * Called when the object is destroyed. It must contains the procedures for removing the object from the world.
	 * If the object is in a collisionhandler, it should be removed from it here.
	 * NOTE: Entity class already removes this entity from the gameWorld and try to remove the entity if it has a collisionHandler.
	 * It will do so if you call the "super" method.
	 * You can avoid this behavior by removing the "super" method invocation.
	 */
	public void removeFromWorld() {
		this.gameWorld.removeEntity(this);
	}
	
	/**
	 * Called by the factory if no entity of this type is available.
	 */
	public abstract Entity clone(boolean enableGraphics);
	
	/**
	 * Teleport the entity to the new position
	 * The collisionHandler will be updated if the entity can be collisioned
	 * @param x
	 * @param y
	 */
	public void teleport(float x, float y) {
		if (this.collisionHandler != null && this.canBeCollisioned()) {
			this.collisionHandler.removeEntity(this);
		}
		
		this.setPosition(x, y);
		
		if (this.collisionHandler != null && this.canBeCollisioned()) {
			this.collisionHandler.addEntity(this);
		}
	}
	
	/**
	 * Called when the entity has touched another entity
	 * @param collisioned
	 * @return
	 */
	public boolean hasTouched(ICollisionable collisioned) {
		return true;
	}
	
	/**
	 * Called when the entity is touched by another entity
	 * @param actor
	 * @return 
	 */
	public boolean isTouched(ICollisionable actor) {
		return true;
	}
	
	public void destroy() {
		this.gameWorld.factory.destroyEntity(this);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final int getEntityID() {
		return this.entityID;
	}
	
	public final void setEntityID(int entityID) {
		this.entityID = entityID;
	}
	
	public final Vector2 getMoveDirection() {
		return this.moveDirection;
	}
	
	public final Vector2 getViewDirection() {
		return this.viewDirection;
	}
	
	public final void setViewDirection(Vector2 viewDirection) {
		this.setViewDirection(viewDirection.x, viewDirection.y);
	}
	
	public void setViewDirection(float viewDirectionX, float viewDirectionY) {
		this.viewDirection.x = viewDirectionX;
		this.viewDirection.y = viewDirectionY;

		this.viewAngle = this.viewDirection.angle();
		this.viewDirection.set(GeometryUtils.angleToVector(this.viewAngle));
	}

	public final void setMoveDirection(Vector2 moveDirection) {
		this.setMoveDirection(moveDirection.x, moveDirection.y);
	}
	
	public void setMoveDirection(float velocityX, float velocityY) {
		this.moveDirection.x = velocityX;
		this.moveDirection.y = velocityY;
		
		if (this.moveDirection.len() > 1) {
			this.moveDirection.set(GeometryUtils.angleToVector(this.moveDirection.angle()));
		}
	}
	
	public final void setMoveDirectionX(float velocityX) {
		this.setMoveDirection(velocityX, this.moveDirection.y);
	}
	
	public final void setMoveDirectionY(float velocityY) {
		this.setMoveDirection(this.moveDirection.x, velocityY);
	}
	
	public final float getViewAngle() {
		return this.viewAngle;
	}
	
	public final float getMoveDirectionX() {
		return this.moveDirection.x;
	}
	
	public final float getMoveDirectionY() {
		return this.moveDirection.y;
	}
	
	public final float getViewDirectionX() {
		return this.viewDirection.x;
	}
	
	public final float getViewDirectionY() {
		return this.viewDirection.y;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public final void setEntityType(int entityType) {
		this.entityType = entityType;
	}
	
	public final int getEntityType() {
		return this.entityType;
	}

	public final void setCollisionHandler(CollisionHandler collisionHandler) {
		this.collisionHandler = collisionHandler;
	}

	public final CollisionHandler getCollisionHandler() {
		return this.collisionHandler;
	}

	public final boolean canBeCollisioned() {
		return canBeCollisioned;
	}

	public final Entity getParentEntity() {
		return parentEntity;
	}

	public final void setParentEntity(Entity parent) {
		this.parentEntity = parent;
	}

	public final GameWorld getGameWorld() {
		return gameWorld;
	}

	public final PlayerData getPlayerData() {
		return this.playerData;
	}
	
	public final void setPlayerData(PlayerData playerData) {
		this.playerData = playerData;
	}
	
	public final boolean isReplicated() {
		return this.replicated;
	}
	
	public final boolean isRunningOnServer() {
		return this.runningOnServer;
	}

	public final boolean isRegistered() {
		return registered;
	}

	public final void setRegistered(boolean registered) {
		this.registered = registered;
	}
	
	public final boolean isVelocityEnabled() {
		return this.velocityEnabled;
	}
	
	public final void setVelocityEnabled(boolean velocityEnabled) {
		this.velocityEnabled = velocityEnabled;
	}
	
}
