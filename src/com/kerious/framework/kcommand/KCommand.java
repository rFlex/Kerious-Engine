package com.kerious.framework.kcommand;

public class KCommand {
	
	////////////////////////
	// VARIABLES
	////////////////

	private static final String localCommandIdentifier = "/";
	private static final String remoteCommandIdentifier = "/remote ";
	
	public final KCommandType type;
	public final String command;
	public final String content;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	private KCommand(KCommandType type, String command, String content) {
		this.type = type;
		this.command = command;
		this.content = content;
	}

	////////////////////////
	// METHODS
	////////////////

	public static boolean isALocalCommand(String command) {
		return command.startsWith(localCommandIdentifier);
	}
	
	public static boolean isARemoteCommand(String command) {
		return command.startsWith(remoteCommandIdentifier);
	}
	
	public static KCommand fromString(String command) {
		String content = "";
		KCommandType type = KCommandType.LOCAL;
		
		if (command.startsWith(remoteCommandIdentifier)) {
			command = command.substring(remoteCommandIdentifier.length());
			final int commandEndIndex = command.indexOf(" ");
					
			if (commandEndIndex > 0) {
				final String commandName = command.substring(0, commandEndIndex);
				content = command.substring(commandEndIndex + 1);
				command = commandName;
			}
			type = KCommandType.REMOTE;
		} else if (command.startsWith(localCommandIdentifier)) {
			command = command.substring(localCommandIdentifier.length());
			final int commandEndIndex = command.indexOf(" ");
					
			if (commandEndIndex > 0) {
				final String commandName = command.substring(0, commandEndIndex);
				content = command.substring(commandEndIndex + 1);
				command = commandName;
			}
		}
		
		return new KCommand(type, command, content);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
