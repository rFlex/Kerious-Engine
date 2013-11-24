package net.kerious.engine.player;

import net.kerious.engine.utils.Controller;

public abstract class Player<T extends PlayerModel> extends Controller<T> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Player() {
		super();
	}

	////////////////////////
	// METHODS
	////////////////
	
	abstract public void update(float deltaTime);
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getName() {
		return this.model.name;
	}
	
	
	public int getId() {
		return this.model.id;
	}
}
