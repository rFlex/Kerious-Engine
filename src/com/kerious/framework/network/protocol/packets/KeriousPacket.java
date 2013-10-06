/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// Packet.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 6:28:38 PM
////////

package com.kerious.framework.network.protocol.packets;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kerious.framework.network.ReliableConnection;
import com.kerious.framework.server.User;

public class KeriousPacket {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	public static class Connection {
		
		public static ConnectionPacket askConnect() {
			ConnectionPacket packet = new ConnectionPacket();//Pools.obtain(ConnectionPacket.class);
			
			packet.setConnectionRequest(ConnectionPacket.CONNECTION_ASK);
			packet.setCode((short)MathUtils.random(Short.MAX_VALUE));
			
			return packet;
		}
		
		public static ConnectionPacket askDisconnect(ReliableConnection serverPeer) {
			ConnectionPacket packet = new ConnectionPacket();
			
			packet.setConnectionRequest(ConnectionPacket.DISCONNECTION_ASK);
			packet.setCode(serverPeer.getCode());
			packet.setIdentifier(serverPeer.getIdentifier());

			return packet;
		}
		
		public static ConnectionPacket acceptConnect(User user) {
			ConnectionPacket packet = new ConnectionPacket();
			
			packet.setConnectionRequest(ConnectionPacket.CONNECTION_ACCEPTED);
			packet.setIdentifier(user.getConnection().getIdentifier());
			packet.setCode(user.getConnection().getCode());
			packet.setPlayerID(user.getPlayerData().getPlayerID());

			return packet;
		}
		
		public static ConnectionPacket acceptDisconnect(User user) {
			ConnectionPacket packet = new ConnectionPacket();
			
			packet.setPlayerID(user.getPlayerData().getPlayerID());
			packet.setConnectionRequest(ConnectionPacket.DISCONNECTION_ACCEPTED);
			packet.setIdentifier(user.getConnection().getIdentifier());
			packet.setCode(user.getConnection().getCode());

			return packet;
		}
		
		public static ConnectionPacket refuseConnectDueToFullServer() {
			ConnectionPacket packet = new ConnectionPacket();//Pools.obtain(ConnectionPacket.class);

			packet.setConnectionRequest(ConnectionPacket.CONNECTION_REFUSED_FULL);
			
			return packet;
		}
		
		public static ConnectionPacket refuseConnectDueToBan() {
			ConnectionPacket packet = new ConnectionPacket();//Pools.obtain(ConnectionPacket.class);

			packet.setConnectionRequest(ConnectionPacket.CONNECTION_REFUSED_BANNED);
			
			return packet;
		}
		
		public static ConnectionInformationPacket identifyServer(String ip, int port) {
			ConnectionInformationPacket packet = new ConnectionInformationPacket();//Pools.obtain(ConnectionInformationPacket.class);
			
			packet.setIP(ip);
			packet.setPort(port);
			
			return packet;
		}

	}
	
	public static class Command {
		
		public static PlayerCommandPacket updateController(Vector2 moveDirection, Vector2 viewDirection, String commandName, String commandContent) {
			PlayerCommandPacket packet = new PlayerCommandPacket();
			
			//packet.setCommandState(controller.isFiring(), 0);
			packet.setMoveDirection(moveDirection);
			packet.setViewDirection(viewDirection);
			packet.setCommandName(commandName);
			packet.setCommandContent(commandContent);
			
			return packet;
		}
		
		public static PlayerCommandPacket sendCommand(String commandName, String commandContent) {
			PlayerCommandPacket packet = new PlayerCommandPacket();
			
			packet.setCommandName(commandName);
			packet.setCommandContent(commandContent);
			
			return packet;
		}
		
		public static KeepAlivePacket keepAlive() {
			KeepAlivePacket packet = new KeepAlivePacket();
			
			return packet;
		}
		
	}
	
	public static class Discover {
		
		public static DiscoverPacket discover() {
			DiscoverPacket packet = new DiscoverPacket();
			
			packet.setDiscover(true);
			
			return packet;
		}
		
		public static DiscoverPacket identify(String mapName, int connectedPlayer, int maxPlayer, int port) {
			DiscoverPacket packet = new DiscoverPacket();
			
			packet.setDiscover(false);
			packet.setServerPort(port);
			packet.setConnectedPlayer(connectedPlayer);
			packet.setMaxPlayer(maxPlayer);
			packet.setMapName(mapName);
			
			return packet;
		}
		
	}

	public static class Information {
		
		public static InformationPacket forbidden() {
			InformationPacket packet = new InformationPacket();
			packet.reset();
			
			packet.setInformation(InformationPacket.FORBIDDEN);
			
			return packet;
		}
		
		public static InformationPacket unsupported() {
			InformationPacket packet = new InformationPacket();
			
			packet.setInformation(InformationPacket.UNSUPPORTED);
			
			return packet;
		}
		
		public static InformationPacket disconnected() {
			InformationPacket packet = new InformationPacket();
			
			packet.setInformation(InformationPacket.DISCONNECTED);
			
			return packet;
		}
		
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
