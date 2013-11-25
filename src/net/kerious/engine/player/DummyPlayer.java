package net.kerious.engine.player;

import net.kerious.engine.entity.Entity;

public class DummyPlayer extends Player {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DummyPlayer() {
		super();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	protected void modelChanged() {
		
	}

	@Override
	public void update(float deltaTime) {
		
	}

	@Override
	public void handleCommand(float directionAngle, float directionStrength, long actions) {
		
	}

	@Override
	public void gainedEntityOwnership(Entity entity) {
		
	}

	@Override
	public void lostEntityOwnership(Entity entity) {
		
	}

	@Override
	public void ready() {
		
	}

	@Override
	public void disconnected() {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
