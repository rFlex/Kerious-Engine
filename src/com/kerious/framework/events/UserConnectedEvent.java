/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.events
// UserConnectedEvent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 1, 2012 at 6:52:37 PM
////////

package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.server.User;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class UserConnectedEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x3;
	private int userID;
	private String userName;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new UserConnectedEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public UserConnectedEvent() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.userID);
		rw.write(this.userName);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.userID = rw.read(this.userID);
		this.userName = rw.read(this.userName);
	}
	
	public static GameEventCreator creator(final User user) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return UserConnectedEvent.create(user);
			}
		};
	}
	
	public static UserConnectedEvent create(int userID, String userName) {
		UserConnectedEvent event = new UserConnectedEvent();

		event.setUserID(userID);
		event.setUserName(userName);
		
		return event;
	}
	
	public static UserConnectedEvent create(User user) {
		UserConnectedEvent event = new UserConnectedEvent();

		event.setUserID(user.getPlayerData().getPlayerID());
		event.setUserName(user.getPlayerData().getPlayerName());
		
		return event;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final int getUserID() {
		return userID;
	}

	public final void setUserID(int userID) {
		this.userID = userID;
	}

	public final String getUserName() {
		return this.userName;
	}

	public final void setUserName(String userName) {
		this.userName = userName;
	}
}
