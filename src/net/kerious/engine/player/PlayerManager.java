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
import net.kerious.engine.KeriousException;
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
	private Class<?> playerClass;
	private Class<?> playerModelClass;
	private PlayerManagerListener listener;
	private EventManager eventManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public PlayerManager() {
		this.players = new IntMap<Player>();
		this.playersAsArray = new SnapshotArray<Player>(true, 32, Player.class);

		this.setPlayerClass(null);
		this.setPlayerModelClass(null);
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
					addPlayer(playerJoinedEvent.playerId, playerJoinedEvent.name);
				}
			});
			this.eventManager.addListener(Events.PlayerLeft, new EventListener() {
				public void onEventFired(EventManager eventManager, Event event) {
					PlayerLeftEvent playerLeftEvent = (PlayerLeftEvent)event;
					removePlayer(playerLeftEvent.playerId, playerLeftEvent.reason);
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
			PlayerJoinedEvent.createAndFire(this.eventManager, id, name);
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
		try {
			return (Player) this.playerClass.newInstance();
		} catch (Exception e) {
			throw new KeriousException("Failed to instantiate Player", e);
		}
	}

	@Override
	protected PlayerModel newModel() {
		try {
			return (PlayerModel) this.playerModelClass.newInstance();
		} catch (Exception e) {
			throw new KeriousException("Failed to instantiate PlayerModel", e);
		}
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public <T extends Player> void setPlayerClass(Class<T> playerClass) {
		if (playerClass == null) {
			this.playerClass = DummyPlayer.class;
		} else {
			this.playerClass = playerClass;
		}
	}
	
	public Class<?> getPlayerClass() {
		return this.playerClass;
	}
	
	public <T extends PlayerModel> void setPlayerModelClass(Class<T> playerModelClass) {
		if (playerModelClass == null) {
			this.playerModelClass = PlayerModel.class;
		} else {
			this.playerModelClass = playerModelClass;
		}
	}
	
	public Class<?> getPlayerModelClass() {
		return this.playerModelClass;
	}
	
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

}
