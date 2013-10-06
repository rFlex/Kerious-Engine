package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.server.User;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class ChatMessageEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////
 
	public static final byte byteIdentifier = 0x6;
	public static final int SERVER_INFORMATION = -1;
	public static final int SERVER_WARNING = -2;
	protected int sender;
	protected String message;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new ChatMessageEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	private ChatMessageEvent() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.sender);
		rw.write(this.message);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.sender = rw.read(sender);
		this.message = rw.read(message);
	}
	
	public static ChatMessageEvent create(int sender, String message) {
		ChatMessageEvent event = new ChatMessageEvent();
		
		event.sender = sender;
		event.message = message;
		
		return event;
	}
	
	public static ChatMessageEvent create(User sender, String message) {
		ChatMessageEvent event = new ChatMessageEvent();
		
		if (sender != null) {
			event.sender = sender.getPlayerData().getPlayerID();
		} else {
			event.sender = -1;
		}
		
		event.message = message;
		
		return event;
	}
	
	public static GameEventCreator creator(final User sender, final String message) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return ChatMessageEvent.create(sender, message);
			}
		};
	}
	
	public static GameEventCreator creator(final int sender, final String message) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return ChatMessageEvent.create(sender, message);
			}
		};
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int getSenderID() {
		return this.sender;
	}
	
	public void setSenderID(int sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
