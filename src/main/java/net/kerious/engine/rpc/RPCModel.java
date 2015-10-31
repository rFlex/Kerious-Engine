/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.rpc
// RPCCall.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 4:23:28 PM
////////

package net.kerious.engine.rpc;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.KeriousSerializableData;
import net.kerious.engine.world.WorldObject;

import com.badlogic.gdx.utils.Array;

public class RPCModel extends KeriousSerializableData {

	////////////////////////
	// CONSTANTS
	////////////////
	
	final public static byte DestinationObject = 0x1;
	final public static byte DestinationType = 0x2;
	
	final public static byte TypeNull = 0x0;
	final public static byte TypeString = 0x1;
	final public static byte TypeInt = 0x2;
	final public static byte TypeFloat = 0x3;
	final public static byte TypeWorldObject = 0x4;

	////////////////////////
	// VARIABLES
	////////////////
	
	public int id;
	public byte destinationType;
	public int destination;
	public String methodName;
	public Array<Object> parameters;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RPCModel() {
		this.parameters = new Array<Object>();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer)
			throws IOException {
		this.id = buffer.getInt();
		this.destinationType = buffer.get();
		this.destination = buffer.getInt();
		this.methodName = this.getString(buffer);

		Array<Object> parameters = this.parameters;
		int size = buffer.getInt();
		
		for (int i = 0; i < size; i++) {
			byte type = buffer.get();
			Object parameter = null;
			
			switch (type) {
			case TypeNull:
				break;
			case TypeString:
				parameter = this.getString(buffer);
				break;
			case TypeInt:
				parameter = buffer.getInt();
				break;
			case TypeFloat:
				parameter = buffer.getFloat();
				break;
			case TypeWorldObject:
				int worldObjectId = buffer.getInt();
				parameter = protocol.getWorldDatabase().get(worldObjectId);
				break;
			}
			
			parameters.add(parameter);
		}
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		buffer.putInt(this.id);
		buffer.put(this.destinationType);
		buffer.putInt(this.destination);
		this.putString(buffer, this.methodName);
		
		Array<Object> parameters = this.parameters;
		buffer.putInt(parameters.size);
	
		for (int i = 0, length = parameters.size; i < length; i++) {
			Object parameter = parameters.items[i];
			
			if (parameter == null) {
				buffer.put(TypeNull);
			} else if (parameter instanceof String) {
				buffer.put(TypeString);
				this.putString(buffer, (String)parameter);
			} else if (parameter instanceof Integer) {
				buffer.put(TypeInt);
				buffer.putInt((Integer)parameter);
			} else if (parameter instanceof Float) {
				buffer.put(TypeFloat);
				buffer.putFloat((Float)parameter);
			} else if (parameter instanceof WorldObject) {
				buffer.put(TypeWorldObject);
				buffer.putInt(((WorldObject)parameter).getId());
			} else {
				System.err.println("Unsupported parameter type: " +  parameter.getClass().getSimpleName() + ". Null will be set instead");
				buffer.put(TypeNull);
			}
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
