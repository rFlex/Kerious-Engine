/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.gamecontroller
// AnalogPad.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 1, 2013 at 10:34:19 PM
////////

package net.kerious.engine.gamecontroller;

import java.io.IOException;
import java.nio.ByteBuffer;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;

import com.badlogic.gdx.math.Vector2;

public class AnalogPad extends KeriousSerializableData {

	////////////////////////
	// VARIABLES
	////////////////
	
	private float strength;
	private float angle;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AnalogPad() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		this.strength = buffer.getFloat();
		this.angle = buffer.getFloat();
	}

	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putFloat(this.strength);
		buffer.putFloat(this.angle);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	/**
	 * Set the strength and angle using a vector. This assume the vector 
	 * have both x and y coordinates between -1 and 1 inclusive.
	 * @param vector
	 */
	public void set(Vector2 vector) {
		if (vector == null) {
			throw new NullArgumentException("vector");
		}
		
		this.setAngle(vector.angle());
		this.setStrength(vector.len());
	}
	
	/**
	 * Get the strength of the AnalogPad, between -1 and 1 inclusive.
	 * @param strength
	 */
	public float getStrength() {
		return strength;
	}

	/**
	 * Set the strength of the AnalogPad, between -1 and 1 inclusive.
	 * @param strength
	 */
	public void setStrength(float strength) {
		if (strength < -1) {
			strength = -1;
		} else if (strength > 1) {
			strength = 1;
		}
		
		this.strength = strength;
	}

	/**
	 * Get the angle of the Pad, in degrees between 0 inclusive and 360 exclusive
	 * @return
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * et the angle of the Pad, in degrees between 0 inclusive and 360 exclusive.
	 * @param angle
	 */
	public void setAngle(float angle) {
		while (angle < 0) {
			angle += 360f;
		}
		
		while (angle >= 360f) {
			angle -= 360f;
		}
		
		this.angle = angle;
	}
}
