/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol.packets
// PlayerActionsPacket.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 28, 2012 at 3:21:15 PM
////////

package com.kerious.framework.network.protocol.packets;

import com.badlogic.gdx.math.Vector2;
import com.kerious.framework.network.protocol.KeriousReliableUDPPacket;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;
import com.kerious.framework.world.entities.Entity;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

public class PlayerCommandPacket extends KeriousReliableUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////

	public static final byte byteIdentifier = 0x14;
	private boolean[] commands;
	private Vector2 moveDirection;
	private Vector2 viewDirection;
	private String commandName;
	private String commandContent;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new PlayerCommandPacket();
		}
		
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public PlayerCommandPacket() {
		super(byteIdentifier);
		
		this.commands = new boolean[8];
		this.moveDirection = new Vector2();
		this.viewDirection = new Vector2();
	}
	
	////////////////////////
	// METHODS
	////////////////

	@Override
	public void reset() {
		super.reset();
		
		for (int i = 0; i < this.commands.length; i++) {
			this.commands[i] = false;
		}
		
		this.moveDirection.x = 0;
		this.moveDirection.y = 0;
		this.viewDirection.x = 0;
		this.viewDirection.y = 0;
		this.commandName = "";
		this.commandContent = "";
	}
	
	@Override
	protected void childUnpack() {
		super.childUnpack();
		
		byte content = 0;
		content = this.read(content);
		
		this.commands[0] = (content & 1) != 0;
		this.commands[1] = (content & (1 << 1)) != 0;
		this.commands[2] = (content & (1 << 2)) != 0;
		this.commands[3] = (content & (1 << 3)) != 0;
		this.commands[4] = (content & (1 << 4)) != 0;
		this.commands[5] = (content & (1 << 5)) != 0;
		this.commands[6] = (content & (1 << 6)) != 0;
		this.commands[7] = (content & (1 << 7)) != 0;
		
		this.moveDirection.x = ((float)this.read(content)) / 100f;
		this.moveDirection.y = ((float)this.read(content)) / 100f;
		this.viewDirection.x = ((float)this.read(content)) / 100f;
		this.viewDirection.y = ((float)this.read(content)) / 100f;
		
		this.commandName = read(commandName);
		this.commandContent = read(commandContent);
	}

	@Override
	protected void childPack() {
		super.childPack();
		
		byte content = 0;
		
		content |= (commands[0] ? 1 : 0);
		content |= (commands[1] ? 1 : 0) << 1;
		content |= (commands[2] ? 1 : 0) << 2;
		content |= (commands[3] ? 1 : 0) << 3;
		content |= (commands[4] ? 1 : 0) << 4;
		content |= (commands[5] ? 1 : 0) << 5;
		content |= (commands[6] ? 1 : 0) << 6;
		content |= (commands[7] ? 1 : 0) << 7;

		write(content);
		
		write((byte)(this.moveDirection.x * 100f));
		write((byte)(this.moveDirection.y * 100f));
		write((byte)(this.viewDirection.x * 100f));
		write((byte)(this.viewDirection.y * 100f));
		
		write(this.commandName);
		write(this.commandContent);
	}
	
	public void updateEntity(Entity entity) {
		entity.setViewDirection(this.viewDirection);
		entity.setMoveDirection(this.moveDirection);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public int size() {
		return super.size() + (sizeof((byte)0) * 5) + sizeof(this.commandName) + sizeof(this.commandContent);
	}

	public final void setCommandState(boolean value, int position) {
		this.commands[position] = value;
	}
	
	public final boolean getCommandState(int position) {
		return this.commands[position];
	}
	
	public final int getCommandSize() {
		return this.commands.length;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandContent() {
		return commandContent;
	}

	public void setCommandContent(String commandContent) {
		this.commandContent = commandContent;
	}

	public final Vector2 getMoveDirection() {
		return moveDirection;
	}

	public final void setMoveDirection(Vector2 direction) {
		this.moveDirection.x = direction.x;
		this.moveDirection.y = direction.y;
	}

	public final Vector2 getViewDirection() {
		return viewDirection;
	}

	public final void setViewDirection(Vector2 view) {
		this.viewDirection.x = view.x;
		this.viewDirection.y = view.y;
	}
}
