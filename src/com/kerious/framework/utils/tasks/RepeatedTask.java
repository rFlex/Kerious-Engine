package com.kerious.framework.utils.tasks;

public abstract class RepeatedTask extends Task {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RepeatedTask(float timeBetweenAction) {
		super(timeBetweenAction);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	protected void update() {
		super.update();
		
		if (this.elapsed) {
			this.stopWatch.start();
			this.elapsed = false;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
