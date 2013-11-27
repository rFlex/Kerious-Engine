/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// PlayerManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 6:36:49 PM
////////

package net.kerious.engine.player;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.utils.ControllerFactory;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventListener;
import net.kerious.engine.world.event.EventListenerRegisterer;
import net.kerious.engine.world.event.EventManager;
import net.kerious.engine.world.event.Events;
import net.kerious.engine.world.event.PlayerJoinedEvent;
import net.kerious.engine.world.event.PlayerLeftEvent;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.SnapshotArray;

public class PlayerManager extends ControllerFactory<Player, PlayerModel>
							implements PlayerModelCreator, EventListenerRegisterer {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private IntMap<Player> players;
	final private SnapshotArray<Player> playersAsArray;
	private PlayerManagerListener listener;
	private PlayerManagerDelegate delegate;
	private EventManager eventManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public PlayerManager() {
		this.players = new IntMap<Player>();
		this.playersAsArray = new SnapshotArray<Player>(true, 32, Player.class);
		
		this.setDelegate(null);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void update(float deltaTime) {
		Player[] players = this.playersAsArray.begin();
		for (int i = 0, length = this.playersAsArray.size; i < length; i++) {
			Player player = players[i];
			player.update(deltaTime);
		}
		this.playersAsArray.end();
	}
	
	@Override
	public void registerEventListeners(EventManager eventManager) {
		this.eventManager = eventManager;

		if (!eventManager.canGenerateEvent()) {
			this.eventManager.addListener(Events.PlayerJoined, new EventListener() {
				public void onEventFired(EventManager eventManager, Event event) {
					PlayerJoinedEvent playerJoinedEvent = (PlayerJoinedEvent)event;
					addPlayer(playerJoinedEvent.id, playerJoinedEvent.name);
				}
			});
			this.eventManager.addListener(Events.PlayerLeft, new EventListener() {
				public void onEventFired(EventManager eventManager, Event event) {
					PlayerLeftEvent playerLeftEvent = (PlayerLeftEvent)event;
					removePlayer(playerLeftEvent.id, playerLeftEvent.reason);
				}
			});
		}
	}
	
	@Override
	public PlayerModel createPlayerModel() {
		return this.createModel();
	}
	
	public Player getPlayer(int id) {
		return this.players.get(id);
	}
	
	public void updatePlayer(PlayerModel playerModel) {
		Player actualPlayer = this.getPlayer(playerModel.id);
		
		if (actualPlayer != null) {
			actualPlayer.setModel(playerModel);
		}
	}
	
	/**
	 * Add a player. This method is automatically called when a player joins the game
	 * @param id
	 * @param name
	 * @return
	 */
	public Player addPlayer(int id, String name) {
		PlayerModel model = this.createModel();
		
		model.id = id;
		model.name = name;
		
		Player player = this.createController(model);
		
		this.players.put(model.id, player);
		this.playersAsArray.add(player);
		
		if (this.eventManager.canGenerateEvent()) {
			PlayerJoinedEvent.createAndFire(this.eventManager, model.id);
		}
		
		if (this.listener != null) {
			this.listener.onPlayerConnected(player);
		}
		
		return player;
	}
	
	public void removePlayer(Player player, String reason) {
		if (player == null) { 
			throw new NullArgumentException("player");
		}
		
		int playerId = player.getId();
		
		this.players.remove(playerId);
		this.playersAsArray.removeValue(player, true);
		
		player.disconnected();
		
		if (this.listener != null) {
			this.listener.onPlayerDisconnected(player, reason);
		}
		
		if (this.eventManager.canGenerateEvent()) {
			PlayerLeftEvent.createAndFire(this.eventManager, playerId, reason);
		}
		
		player.release();
	}
	
	/**
	 * Remove a player. This method is automatically called when a player
	 * leaves the game. Calling this method will NOT disconnect a player.
	 * @param id
	 * @param reason
	 * @return
	 */
	public boolean removePlayer(int id, String reason) {
		Player player = this.getPlayer(id);
		
		if (player != null) {
			
		}
		
		return false;
	}
	
	@Override
	protected Player newController() {
		return this.delegate.newPlayerController();
	}

	@Override
	protected PlayerModel newModel() {
		return this.delegate.newPlayerModel();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public PlayerManagerListener getListener() {
		return listener;
	}

	public void setListener(PlayerManagerListener listener) {
		this.listener = listener;
	}
	
	public Iterable<Player> getPlayers() {
		return this.players.values();
	}
	
	public int getPlayersCount() {
		return this.players.size;
	}

	/**
	 * Get the delegate that is responsible for creating players and player models
	 * @return
	 */
	public PlayerManagerDelegate getDelegate() {
		return delegate;
	}

	/**
	 * Change the delegate that is responsible for creating players and player models
	 * If you want to create your own players and player model you are in the good place!
	 * Passing null will automatically set a delegate that creates dummy players
	 * (who don't have any logic implemented) and creates the built-in default PlayerModel models
	 * @param delegate
	 */
	public void setDelegate(PlayerManagerDelegate delegate) {
		if (delegate == null) {
			delegate = new DummyPlayerManagerDelegate();
		}
		
		this.delegate = delegate;
	}

}
