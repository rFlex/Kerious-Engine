package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;

public class GameEvent {

	////////////////////////
	// VARIABLES
	////////////////

	final public byte eventType;
	protected int eventID;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static interface GameEventCreator {
		GameEvent create();
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public GameEvent(byte eventType) {
		this.eventType = eventType;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	
	public void packIn(ReaderWriter rw) {
		rw.write(this.eventID);
	}
	
	public void unpackFrom(ReaderWriter rw) {
		this.eventID = rw.read(this.eventID);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
	
	public int getEventID() {
		return this.eventID;
	}

}
