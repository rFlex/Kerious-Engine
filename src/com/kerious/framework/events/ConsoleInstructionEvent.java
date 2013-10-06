package com.kerious.framework.events;

import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class ConsoleInstructionEvent extends GameEvent {

	////////////////////////
	// VARIABLES
	////////////////
 
	public static final byte byteIdentifier = 0x7;
	protected String consoleCommand;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<GameEvent> {

		@Override
		public GameEvent instanciate() {
			return new ConsoleInstructionEvent();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ConsoleInstructionEvent() {
		super(byteIdentifier);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void packIn(ReaderWriter rw) {
		super.packIn(rw);
		
		rw.write(this.consoleCommand);
	}
	
	@Override
	public void unpackFrom(ReaderWriter rw) {
		super.unpackFrom(rw);
		
		this.consoleCommand= rw.read(consoleCommand);
	}
	
	public static GameEventCreator creator(final String command) {
		return new GameEventCreator() {
			
			@Override
			public GameEvent create() {
				return ConsoleInstructionEvent.create(command);
			}
		};
	}
	
	public static ConsoleInstructionEvent create(String command) {
		ConsoleInstructionEvent event = new ConsoleInstructionEvent();

		event.setConsoleCommand(command);
		
		return event;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final String getConsoleCommand() {
		return consoleCommand;
	}

	public final void setConsoleCommand(String consoleCommand) {
		this.consoleCommand = consoleCommand;
	}
}
