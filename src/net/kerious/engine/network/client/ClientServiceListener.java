/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.network.client
// KeriousProtocolClientListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 6:03:53 PM
////////

package net.kerious.engine.network.client;

import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.player.Player;
import net.kerious.engine.world.event.Event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public interface ClientServiceListener {

	void onReceivedSnapshot(ClientService clientService, Array<Player> players, Array<EntityModel> entityModels, Array<Event> events);
	void onReceivedWorldInformations(ClientService clientService, ObjectMap<String, String> informations, boolean shouldLoadWorld);
	void onReceivedInformation(ClientService clientService, String informationType, String message);
	void onDisconnected(ClientService clientService, String ip, int port, String reason);
	void onConnected(ClientService clientService, String ip, int port, int playerId);
	void onConnectionFailed(ClientService clientService, String ip, int port, String reason);
	
}
