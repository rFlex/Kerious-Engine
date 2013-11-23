/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world
// PlayerManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 6:36:49 PM
////////

package net.kerious.engine.player;

import me.corsin.javatools.misc.Pool;

import com.badlogic.gdx.utils.IntMap;

public class PlayerManager implements PlayerCreator {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private IntMap<Player> players;
	final private Pool<Player> playersPool;
	private PlayerManagerListener listener;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public PlayerManager() {
		this.players = new IntMap<Player>();
		this.playersPool = new Pool<Player>() {
			protected Player instantiate() {
				return newPlayer();
			}
		};
		this.players.values();
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Override to implement your own player
	 * @return
	 */
	protected Player newPlayer() {
		return new Player();
	}

	@Override
	public Player createPlayer() {
		return this.playersPool.obtain();
	}
	
	public Player getPlayer(int id) {
		return this.players.get(id);
	}
	
	public void updatePlayer(Player player) {
		Player actualPlayer = this.getPlayer(player.id);
		
		if (actualPlayer == null) {
			actualPlayer = this.addPlayer(player.id, player.name);
		}
		
		player.copyTo(actualPlayer);
	}

	public Player addPlayer(int id, String name) {
		Player player = this.createPlayer();
		player.id = id;
		player.name = name;
		
		this.players.put(id, player);
		
		if (this.listener != null) {
			this.listener.onPlayerConnected(player);
		}
		
		return player;
	}
	
	public boolean removePlayer(int id, String reason) {
		Player player = this.players.remove(id);
		
		if (player != null) {
			if (this.listener != null) {
				this.listener.onPlayerDisconnected(player, reason);
			}

			player.release();
			
			return true;
		}
		return false;
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
}
