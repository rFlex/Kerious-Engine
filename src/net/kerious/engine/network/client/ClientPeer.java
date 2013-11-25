/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolPeerServer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 4:54:08 PM
////////

package net.kerious.engine.network.client;

import java.net.InetAddress;

import net.kerious.engine.entity.Entity;
import net.kerious.engine.network.protocol.KeriousProtocolPeer;
import net.kerious.engine.network.protocol.packet.BasicCommandPacket;
import net.kerious.engine.network.protocol.packet.ConnectionPacket;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.network.protocol.packet.RequestPacket;
import net.kerious.engine.network.protocol.packet.SnapshotPacket;
import net.kerious.engine.network.protocol.packet.WorldInformationsPacket;
import net.kerious.engine.player.Player;
import net.kerious.engine.player.PlayerModel;
import net.kerious.engine.world.World;
import net.kerious.engine.world.event.Event;

import com.badlogic.gdx.utils.ObjectSet;

public class ClientPeer extends KeriousProtocolPeer {

	////////////////////////
	// VARIABLES
	////////////////
	
	private int playerId;
	private String name;
	private PeerServerDelegate delegate;
	private boolean readyToReceiveSnapshots;
	private ObjectSet<Event> pendingEvents;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ClientPeer(InetAddress address, int port) {
		super(address, port);
		
		this.pendingEvents = new ObjectSet<Event>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public boolean handlePacketReceived(KeriousPacket packet) {
		if (super.handlePacketReceived(packet)) {
			return true;
		}

		switch (packet.packetType) {
		case KeriousPacket.TypeConnection:
			ConnectionPacket connectionPacket = (ConnectionPacket)packet;
			
			switch (connectionPacket.connectionRequest) {
			case ConnectionPacket.ConnectionAsk:
				// Send the accepted packet
				ConnectionPacket acceptedConnectionPacket = this.protocol.createConnectionPacket(ConnectionPacket.ConnectionAccepted);
				this.send(acceptedConnectionPacket);
				acceptedConnectionPacket.release();
				break;
			case ConnectionPacket.ConnectionInterrupted:
				this.setDisconnectReason(connectionPacket.reason);
				this.setExpired(true);
			default:
				return false;
			}
			break;
		case KeriousPacket.TypeRequest:
			RequestPacket requestPacket = (RequestPacket)packet;
			
			switch (requestPacket.request) {
			case RequestPacket.RequestBeginReceiveSnapshots:
				this.readyToReceiveSnapshots = true;
				break;
			case RequestPacket.RequestEndReceiveSnapshots:
				this.readyToReceiveSnapshots = false;
				break;
			case RequestPacket.RequestReceiveWorldInformations:
				WorldInformationsPacket worldInformationsPacket = this.protocol.createWorldInformationsPacket();
				if (this.delegate != null) {
					this.delegate.fillWorldInformations(this, worldInformationsPacket.informations);
				}
				this.send(worldInformationsPacket);
				worldInformationsPacket.release();
				break;
			default:
				return false;
			}
			break;
		case KeriousPacket.TypeBasicCommand:
			BasicCommandPacket commandPacket = (BasicCommandPacket)packet;
			
			if (this.delegate != null) {
				this.delegate.updateWorldWithCommands(this, commandPacket.directionAngle,
						commandPacket.directionStrength, commandPacket.actionsBitfield);
			}
		break;
		
		default:
			return false;
		}
		return true;
	}
	
	public void sendEvent(Event event) {
		event.retain();
		
		this.pendingEvents.add(event);
	}
	
	public void sendSnapshot(World world) {
		SnapshotPacket snapshotPacket = this.protocol.createSnapshotPacket();
		
		for (Entity<?, ?> entity : world.getEntityManager().getEntites()) {
			snapshotPacket.addModel(entity.getModel());
		}

		for (Player<PlayerModel> player : world.getPlayerManager().getPlayers()) {
			snapshotPacket.addPlayer(player.getModel());
		}

		for (Event event : this.pendingEvents) {
			snapshotPacket.addEvent(event);
		}
		
		this.send(snapshotPacket);
		
		snapshotPacket.release();
	}
	
	protected void packetReceived(KeriousPacket packet) {
		super.packetReceived(packet);
		
		if (packet.packetType == KeriousPacket.TypeSnapshot) {
			SnapshotPacket snapshotPacket = (SnapshotPacket)packet;

			Event[] events = snapshotPacket.events.items;
			for (int i = 0, length = snapshotPacket.events.size; i < length; i++) {
				Event event = events[i];
				
				if (this.pendingEvents.remove(event)) {
					event.release();
				}
			}
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public boolean isReadyToReceiveSnapshots() {
		return readyToReceiveSnapshots;
	}

	public void setReadyToReceiveSnapshots(boolean readyToReceiveSnapshots) {
		this.readyToReceiveSnapshots = readyToReceiveSnapshots;
	}

	public PeerServerDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(PeerServerDelegate delegate) {
		this.delegate = delegate;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
