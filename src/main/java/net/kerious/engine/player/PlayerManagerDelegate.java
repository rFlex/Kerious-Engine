package net.kerious.engine.player;

public interface PlayerManagerDelegate {

	/**
	 * Instantiate a new player model
	 * @return
	 */
	PlayerModel newPlayerModel();
	
	/**
	 * Instantiate a new player controller
	 * @return
	 */
	Player newPlayerController();
	
}
