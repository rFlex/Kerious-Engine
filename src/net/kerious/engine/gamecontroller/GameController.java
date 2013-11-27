/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.gamecontroller
// GameController.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 25, 2013 at 2:35:19 AM
////////

package net.kerious.engine.gamecontroller;

import net.kerious.engine.KeriousException;
import net.kerious.engine.input.KeyboardResponder;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.packet.KeriousPacket;
import net.kerious.engine.networkgame.CommandPacketCreator;

import com.badlogic.gdx.Input.Keys;

public class GameController implements KeyboardResponder, CommandPacketCreator {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final int Unbound = -1;

	private float directionAngle;
	private float directionStrength;
	private int[] binds;
	private long enableActions;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public GameController() {
		this.binds = new int[Keys.F12 + 1];
		
		for (int i = 0; i < this.binds.length; i++) {
			this.binds[i] = Unbound;
		}
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void onBecameResponder() {
		
	}

	@Override
	public void onResignedResponder() {
		
	}
	
	final private void checkKeycode(int keycode) {
		if (keycode > this.binds.length || keycode < 0) {
			throw new KeriousException("keycode out of bound (" + keycode + ")");
		}
	}
	
	public void bind(int keycode, int action) {
		if (action < 0 || action >= 64) {
			throw new KeriousException("Action must be a constant between 0 included and 64 not included");
		}
		this.checkKeycode(keycode);
		
		this.binds[keycode] = action;
	}
	
	public void unbind(int keycode) {
		this.checkKeycode(keycode);
		
		this.binds[keycode] = Unbound;
	}

	@Override
	public void onKeyDown(int keycode) {
		int action = this.binds[keycode];
		
		if (action != Unbound) {
			this.enableActions |= (long)0x1 << action;
		}
	}

	@Override
	public void onKeyUp(int keycode) {
		int action = this.binds[keycode];

		if (action != Unbound) {
			this.enableActions &= ~((long)0x1 << action);
		}
	}

	@Override
	public void onCharTyped(char c) {
		
	}
	
	public KeriousPacket generateCommandPacket(KeriousProtocol protocol) {
		return protocol.createCommandPacket(this.directionAngle, this.directionStrength, this.enableActions);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public long getActions() {
		return this.enableActions;
	}
	
	public float getDirectionAngle() {
		return directionAngle;
	}

	public void setDirectionAngle(float directionAngle) {
		this.directionAngle = directionAngle;
	}

	public float getDirectionStrength() {
		return directionStrength;
	}

	public void setDirectionStrength(float directionStrength) {
		if (directionStrength > 1) {
			directionStrength = 1;
		} else if (directionStrength < 0) {
			directionStrength = 0;
		}
		
		this.directionStrength = directionStrength;
	}
}
