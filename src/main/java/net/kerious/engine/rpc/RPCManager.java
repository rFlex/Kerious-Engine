/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.rpc
// RPCManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 4:22:14 PM
////////

package net.kerious.engine.rpc;

import java.lang.reflect.Method;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.world.WorldObject;
import net.kerious.engine.world.WorldObjectDatabase;

public class RPCManager {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static final int DestinationAny = -1;
	
	private WorldObjectDatabase woDatabase;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public RPCManager(WorldObjectDatabase woDatabase) {
		if (woDatabase == null) {
			throw new NullArgumentException("woDatabase");
		}
		
		this.woDatabase = woDatabase;
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Scan the worldObjectClass for RPCs. The RPCManager will save some informations needed
	 * for making this class available for future RPCs.
	 * @param worldObjectClass
	 */
	public <T extends WorldObject> void makeClassAvailableForRPC(Class<T> worldObjectClass, int type) {
		if (worldObjectClass == null) {
			throw new NullArgumentException("worldObjectClass");
		}
		
		Method[] methods = worldObjectClass.getMethods();
		for (int i = 0, length = methods.length; i < length; i++) {
			Method method = methods[i];
			RPC annotation = method.getAnnotation(RPC.class);
			
			if (annotation != null) {
				
			}
		}
	}
	
	/**
	 * Call the methodName on the worldObject.
	 * @param method
	 * @param worldObject
	 * @param params
	 */
	public void call(String method, WorldObject worldObject, Object ... params) {
		if (worldObject == null) {
			throw new NullArgumentException("worldObject");
		}
		
		this.call(method, worldObject.getId(), params);
	}
	
	/**
	 * Call the methodName on destinationId. If the destinationId is not found,
	 * the rpc will be cancelled 
	 * @param method
	 * @param destinationId
	 * @param destinationType
	 * @param arg0
	 */
	public void call(String method, int destinationId, Object ... params) {
		
	}
	
	/**
	 * Call the methodName on every instances of the destinationType
	 * @param method
	 * @param destinationType
	 * @param params
	 */
	public void callOnType(String method, int destinationType, Object ... params) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
