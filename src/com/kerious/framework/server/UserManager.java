/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// UserManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 25, 2012 at 7:18:24 PM
////////

package com.kerious.framework.server;

import java.util.ArrayList;
import java.util.Iterator;

import com.kerious.framework.events.IEventPropagator;
import com.kerious.framework.events.UserConnectedEvent;
import com.kerious.framework.events.UserDisconnectedEvent;
import com.kerious.framework.events.GameEvent.GameEventCreator;
import com.kerious.framework.network.ReliableConnection;
import com.kerious.framework.network.ReliableConnectionManager;
import com.kerious.framework.network.NetworkPeer;
import com.kerious.framework.network.protocol.packets.KeriousPacket;
import com.kerious.framework.world.GameWorld;

public class UserManager implements Iterable<User>, IEventPropagator {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final KeriousServer keriousServer;
	private final ReliableConnectionManager peerManager;
	private final ArrayList<User> users;
	private int currentUserIDSequence;
	private int maxUsers;
	private GameWorld replicatedWorld;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public UserManager(KeriousServer keriousServer) {
		this.keriousServer = keriousServer;
		this.peerManager = new ReliableConnectionManager();
		this.users = new ArrayList<User>();
	}

	////////////////////////
	// METHODS
	////////////////

	public User getUserForName(String playerName) {
		for (User user : this.users) {
			if (user.getPlayerData().getPlayerName().equals(playerName)) {
				return user;
			}
		}
		
		return null;
	}
	
	private String filterPlayerName(String playerName) {
		if (playerName != null && playerName.startsWith("#")) {
			playerName = playerName.substring(1, playerName.length());
		}
		
		if (playerName == null || playerName.matches("^\\s*$")) {
			playerName = "Nameless";
		}
		
		int currentPos = 0;
		
		for (;;) {
			User user = null;
			
			if (currentPos == 0) {
				user = this.getUserForName(playerName);
			} else {
				user = this.getUserForName(playerName + "-" + currentPos);
			}
			
			if (user != null) {
				currentPos++;
			} else {
				break;
			}
		}
		
		if (currentPos > 0) {
			playerName = playerName + "-" + currentPos;
		}
		
		return playerName;
	}
	
	private User createUser(String playerName, ReliableConnection networkPeer) {
		playerName = this.filterPlayerName(playerName);
		
		User user = new User(this.keriousServer, this.currentUserIDSequence);
		user.getPlayerData().setPlayerName(playerName);
		this.currentUserIDSequence++;
		
		user.setConnection(networkPeer);
		
		if (networkPeer.isConnected()) {
			networkPeer.send(KeriousPacket.Connection.acceptConnect(user));
		}
		
		for (User othUser : this.users) {
			user.sendEvent(UserConnectedEvent.create(othUser));
		}
		
		this.keriousServer.onUserConnected.call(this.keriousServer, user);
		
		if (networkPeer.isConnected() && this.replicatedWorld != null) {
			user.replicateWorld(this.replicatedWorld);
		}
		
		this.users.add(user);
		
		this.sendEventToUsers(UserConnectedEvent.creator(user));
		
		return user;
	}
	
	public User addLocalUser(String playerName) {
		ReliableConnection authPeer = this.peerManager.createEmptyConnection();

		return this.createUser(playerName, authPeer);
	}
	
	public User addUser(String playerName, short code, NetworkPeer peer) {
		User user = null;
		ReliableConnection authPeer = this.peerManager.createConnection(code, peer);

		if (authPeer != null) {
			user = this.createUser(playerName, authPeer);
		}
		
		return user;
	}
	
	public void removeUser(User user) {
		if (user.getConnection().isConnected()) {
			user.getConnection().send(KeriousPacket.Connection.acceptDisconnect(user));
		}
		
		this.users.remove(user);
		this.peerManager.remove(user.getConnection());

		this.keriousServer.onUserDisconnected.call(this.keriousServer, user);
		
		user.stopReplicatingWorld();
		user.setConnection(null);
		
		this.sendEventToUsers(UserDisconnectedEvent.creator(user));
		
		if (user.getListener() != null) {
			user.getListener().onDisconnected();
		}
	}
	
	public void sendEventToUsers(GameEventCreator creator) {
		for (User user : this.users) {
			if (user.getConnection().isConnected()) {
				user.sendEvent(creator.create());
			}
		}
	}
	
	public void replicateWorldToUsers(GameWorld world) {
		this.replicatedWorld = world;
		
		world.setEventPropagator(this);
		for (User user : this.users) {
			user.replicateWorld(world);
		}
	}
	
	@Override
	public void fireEvent(GameEventCreator event) {
		this.sendEventToUsers(event);
	}
	
	public void stopReplicatingWorldToUsers() {
		for (User user : this.users) {
			user.stopReplicatingWorld();
		}
	}
	
	public final void updateLogic() {
		for (int i = 0, size = this.users.size(); i < size; i++) {
			this.users.get(i).updateLogic();
		}
	}
	
	public final void sendUpdates() {
		for (int i = 0; i < this.users.size(); i++) {
			User user = this.users.get(i);

			if (user.hasTimedOut()) {
				this.removeUser(user);
				i--;
			} else if (user.isReadyForSnapshot()) {
				user.sendTick();
			}
		}
	}
	
	@Override
	public Iterator<User> iterator() {
		return users.iterator();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final User getUserFromIdent(short ident, short code) {
		ReliableConnection reliableConnection = this.peerManager.getFromIdent(ident, code);
		
		return reliableConnection != null ? (User)reliableConnection.getPacketListener() : null;
	}
	
	public final User getUserForPosition(int index) {
		return this.users.get(index);
	}
	
	public final int getConnectedUsers() {
		return this.users.size();
	}
	
	public final int getMaxUsers() {
		return this.maxUsers;
	}
	
	public final void setMaxUsers(int size) {
		this.maxUsers = size;
	}
}
