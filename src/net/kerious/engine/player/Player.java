package net.kerious.engine.player;

import net.kerious.engine.entity.Entity;
import net.kerious.engine.utils.Controller;
import net.kerious.engine.world.World;

public abstract class Player extends Controller<PlayerModel> {

	////////////////////////
	// VARIABLES
	////////////////
	
	protected World world;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Player() {
		super();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public static final boolean isActionActivated(long actions, int action) {
		return (actions & (long)0x1 << action) != 0;
	}
	
	/**
	 * Create an Entity that has this player as owner
	 * @param entityType
	 * @return
	 */
	public Entity createEntity(int entityType) {
		Entity entity = this.world.createEntity(entityType);
		entity.setPlayer(this);
		
		return entity;
	}
	
	/**
	 * Called when the player has disconnected
	 * If the player holds any Entity, they must be removed here
	 */
	abstract public void disconnected();

	/**
	 * Update the Player logic
	 * @param deltaTime
	 */
	abstract public void update(float deltaTime);
	
	/**
	 * 
	 * @param directionAngle
	 * @param directionStrength
	 * @param actions
	 */
	abstract public void handleCommand(float directionAngle, float directionStrength, long actions);
	
	abstract public void gainedEntityOwnership(Entity entity);
	
	abstract public void lostEntityOwnership(Entity entity);
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getName() {
		return this.model.name;
	}
	
	public int getId() {
		return this.model.id;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
