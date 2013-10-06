/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.server.console
// ServerConsole.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2012 at 9:55:30 PM
////////

package com.kerious.framework.server;

import com.kerious.framework.console.Console;
import com.kerious.framework.console.ConsoleCommand;
import com.kerious.framework.console.ConsoleCommandChangeEvent;
import com.kerious.framework.console.UserConsoleCommand;
import com.kerious.framework.events.ChatMessageEvent;
import com.kerious.framework.utils.IEventListener;

public class ServerConsole extends Console {

	////////////////////////
	// VARIABLES
	////////////////

	protected KeriousServer server;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ServerConsole() {
		this(null);
	}
	
	public ServerConsole(KeriousServer server) {
		super();
		
		this.server = server;
		
		this.registerCommand(new Status());
		this.registerCommand(new FPS());
		this.registerCommand(new MaxPlayers());
		this.registerCommand(new Kick());
		this.registerCommand(new Say());
		this.registerCommand(new NetUpdateRate());
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void setServer(KeriousServer server) {
		this.server = server;
		
		if (server != null) {
			server.onUserConnected.addListener(new IEventListener<User>() {
				
				@Override
				public void onFired(Object sender, User user) {
					printMessage("User #" + user.getPlayerData().getPlayerID() + " called " + user.getPlayerData().getPlayerName() + " connected.");
				}
			});
			server.onUserDisconnected.addListener(new IEventListener<User>() {
				
				@Override
				public void onFired(Object sender, User user) {
					printMessage("User #" + user.getPlayerData().getPlayerID() + " called " + user.getPlayerData().getPlayerName() + " disconnected.");
				}
			});
		}
	}
	
	@Override
	public void printMessage(String message) {
		System.out.println("[SERVER] " + message);
	}
	
	////////////////////////
	// COMMANDS
	////////////////

//	private class AddBot extends ConsoleCommand {
//
//		public AddBot() {
//			super("addbot", false);
//		}
//
//		@Override
//		protected void onCommandChanged(ConsoleCommandChangeEvent arg) {
//			String[] position = arg.newValue.split(" ");
//			
//			if (position.length == 2) {
//				server.getBotManager().addBot(getAsFloat(position[0]), getAsFloat(position[1]));
//			} else {
//				arg.cancelChange("Addbot needs position x and y");
//			}
//		}
//	}
//	
	private class MaxPlayers extends ConsoleCommand {

		public MaxPlayers() {
			super("sv_maxplayers", true);
		}

		@Override
		protected void onCommandChanged(ConsoleCommandChangeEvent arg) {
			int maxPlayer = getAsInt(arg.newValue);
			
			if (maxPlayer < 0) {
				arg.newValue = Integer.toString(0);
				maxPlayer = 0;
			}
			
			server.userManager.setMaxUsers(maxPlayer);
		}
		
	}
	
	private class Status extends ConsoleCommand {

		public Status() {
			super("status", false);
		}
		
		@Override
		protected void onCommandChanged(ConsoleCommandChangeEvent arg) throws Exception {
			String message = "=== Status ===";
			
			message += "\nPlayers: " + ServerConsole.this.server.userManager.getConnectedUsers() + "/" + ServerConsole.this.server.userManager.getMaxUsers();
			message += "\nServer FPS: " + ServerConsole.this.server.getCurrentFPS();
			
			for (User user : ServerConsole.this.server.userManager) {
				int ping = user.getConnection() != null ? user.getConnection().getPing() : 0;
				message += "\n#" + user.getPlayerData().getPlayerID() + " - " + user.getPlayerData().getPlayerName() + " (ping: " + ping + ")";
			}
			
			arg.setMessage(message);
		}
		
	}
	
	private class FPS extends ConsoleCommand {

		public FPS() {
			super("fps_max", true);
		}
		
		@Override
		protected void onCommandChanged(ConsoleCommandChangeEvent arg) throws Exception {
			int fps = getAsInt(arg.newValue);
			
			if (fps < 1) {
				arg.cancelChange("FPS must be equal or more to 1");
			} else {
				ServerConsole.this.server.setFPS(fps);
				arg.setMessage("FPS is now " + fps);
			}
		}
		
	}
	
	private class Kick extends ConsoleCommand {

		public Kick() {
			super("kick", false);
		}
		
		@Override
		protected void onCommandChanged(ConsoleCommandChangeEvent arg) throws Exception {
			User user = ServerConsole.this.server.userManager.getUserForName(arg.newValue);
			
			if (user != null) {
				ServerConsole.this.server.userManager.removeUser(user);
			} else {
				arg.cancelChange("User " + arg.newValue + " doesn't exist");
			}
		}
		
	}
	
	private class Say extends UserConsoleCommand {

		public Say() {
			super("say", false);
		}

		@Override
		protected void onCommandChanged(User sender, ConsoleCommandChangeEvent arg) throws Exception {
			sender.getServer().userManager.sendEventToUsers(ChatMessageEvent.creator(sender, arg.newValue));
		}
		
	}
	
	private class NetUpdateRate extends UserConsoleCommand {

		public NetUpdateRate() {
			super("net_updaterate", false);
		}

		@Override
		protected void onCommandChanged(User sender, ConsoleCommandChangeEvent arg) throws Exception {
			float value = getAsFloat(arg.newValue);
			
			if (value < 1) {
				arg.cancelChange("Updaterate cannot be less than 1");
			} else {
				arg.setMessage("Updaterate changed to " + value);
				sender.setUploadRate(value);
			}
		}
		
	}
}
