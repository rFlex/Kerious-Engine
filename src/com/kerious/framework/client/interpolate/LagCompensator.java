/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.client.interpolate
// LagCompensator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 9, 2012 at 3:13:38 PM
////////

package com.kerious.framework.client.interpolate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kerious.framework.client.KeriousPlay;
import com.kerious.framework.network.protocol.packets.EntityState;
import com.kerious.framework.world.entities.Entity;

public class LagCompensator {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<Entity, LagCompensatorHandle> entities;
	private ArrayList<LagCompensatorHandle> handles;
	private KeriousPlay keriousPlay;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LagCompensator(KeriousPlay keriousPlay) {
		this.entities = new HashMap<Entity, LagCompensatorHandle>();
		this.handles = new ArrayList<LagCompensatorHandle>();
		this.keriousPlay = keriousPlay;
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void update() {
		for (LagCompensatorHandle handle : this.handles) {
			handle.saveState();
		}
	}
	
	public void updateEntity(Entity entity, EntityState state, long renderingTime) {
		LagCompensatorHandle handle = this.entities.get(entity);
		
		if (handle != null) {
			handle.updateEntity(state, renderingTime - this.keriousPlay.getPing());
		} else {
			state.exportToEntity(entity);
		}
	}
	
	public void removeAll() {
		this.handles.clear();
		this.entities.clear();
	}
	
	public void compensateLag(Entity entity) {
		this.stopCompensateLag(entity);
		
		LagCompensatorHandle handle = new LagCompensatorHandle(entity);
		
		this.entities.put(entity, handle);
		this.handles.add(handle);
	}
	
	public void stopCompensateLag(Entity entity) {
		LagCompensatorHandle handle = this.entities.get(entity);
		
		if (handle != null) {
			this.entities.remove(entity);
			this.handles.remove(handle);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
