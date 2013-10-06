/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.events
// UserDisconnectedEvent.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 1, 2012 at 7:02:35 PM
////////

package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.server.User;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class UserDisconnectedEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x4;
	private int userID;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new UserDisconnectedEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public UserDisconnectedEvent() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.userID);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.userID = rw.read(this.userID);
	}
	
	public static GameEventCreator creator(final User user) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return UserDisconnectedEvent.create(user);
			}
		};
	}
	
	public static UserDisconnectedEvent create(User user) {
		UserDisconnectedEvent event = new UserDisconnectedEvent();

		event.setUserID(user.getPlayerData().getPlayerID());
		
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

}
