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
import net.kerious.engine.network.protocol.packet.CommandPacket;
import net.kerious.engine.networkgame.CommandPacketCreator;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

public class GameController implements KeyboardResponder, CommandPacketCreator {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final int Unbound = -1;

	private Array<AnalogPad> analogPads;
	private float directionAngle;
	private float directionStrength;
	private int[] binds;
	private KeyListener[] listeners;
	private long actions;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public GameController() {
		this.analogPads = new Array<AnalogPad>(AnalogPad.class);
		this.binds = new int[Keys.F12 + 1];
		this.listeners = new KeyListener[this.binds.length];
		
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
		this.actions = 0;
	}
	
	final private void checkKeycode(int keycode) {
		if (keycode >= this.binds.length || keycode < 0) {
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
		KeyListener listener = this.listeners[keycode];
		
		if (action != Unbound) {
			this.actions |= (long)0x1 << action;
		}
		if (listener != null) {
			listener.onKeyPressed(this, keycode);
		}
	}

	@Override
	public void onKeyUp(int keycode) {
		int action = this.binds[keycode];
		KeyListener listener = this.listeners[keycode];

		if (action != Unbound) {
			this.actions &= ~((long)0x1 << action);
		}
		if (listener != null) {
			listener.onKeyReleased(this, keycode);
		}
	}

	@Override
	public void onCharTyped(char c) {
		
	}
	
	@Override
	public CommandPacket generateCommandPacket(KeriousProtocol protocol) {
		return protocol.createCommandPacket(this.analogPads, this.actions);
	}
	
	/**
	 * Create and add an analog pad.
	 * Pads are sent through the network
	 * @return the created analog pad
	 */
	public AnalogPad addAnalogPad() {
		AnalogPad pad = new AnalogPad();
		
		this.analogPads.add(pad);
		
		return pad;
	}
	
	public void setKeyListener(int keycode, KeyListener keyEventListener) {
		this.checkKeycode(keycode);
		this.listeners[keycode] = keyEventListener;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public AnalogPad getAnalogPad(int index) {
		return this.analogPads.get(index);
	}
	
	public Array<AnalogPad> getAnalogPads() {
		return this.analogPads;
	}
	
	public long getActions() {
		return this.actions;
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
