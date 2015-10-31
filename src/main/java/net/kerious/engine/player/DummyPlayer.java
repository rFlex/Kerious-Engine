package net.kerious.engine.player;

import com.badlogic.gdx.utils.Array;

import net.kerious.engine.entity.Entity;
import net.kerious.engine.gamecontroller.AnalogPad;

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

	@Override
	public void handleCommand(Array<AnalogPad> analogPads, long actions) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
