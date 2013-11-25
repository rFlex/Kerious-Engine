package net.kerious.engine.player;

public class DummyPlayerManagerDelegate implements PlayerManagerDelegate {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DummyPlayerManagerDelegate() {
		super();
	}


	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public PlayerModel newPlayerModel() {
		return new PlayerModel();
	}

	@Override
	public Player newPlayerController() {
		return new DummyPlayer();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
