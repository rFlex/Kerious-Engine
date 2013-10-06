/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world
// GameWorld.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 29 sept. 2012 at 17:17:19
////////

package com.kerious.framework.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.kerious.framework.Application;
import com.kerious.framework.events.GameEvent.GameEventCreator;
import com.kerious.framework.events.IEventPropagator;
import com.kerious.framework.world.entities.Entity;
import com.kerious.framework.world.entities.EntityFactory;
import com.kerious.framework.world.tmx.TMXMap;

public class GameWorld extends StageGroup implements IEventPropagator {

	////////////////////////
	// VARIABLES
	////////////////

	public final Application application;
	public final EntityFactory factory;
	private IEventPropagator eventPropagator;
	private TrackingCamera trackingCamera;
	private Group aerialLayer; // Rendered and updated manually.
	private TMXMap map;
	private Group[] layers;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public GameWorld(Application game, int layer) {
		this(game.width, game.height, game, layer);
	}
	
	public GameWorld(float width, float height, Application application, int layer) {
		super(width, height, application.batch, application.drawingEnabled);
		
		if (application.drawingEnabled) {
			this.trackingCamera = new TrackingCamera(width, height);
			this.trackingCamera.position.x = width / 2;
			this.trackingCamera.position.y = height / 2;
			this.setCamera(this.trackingCamera);
		}
		
		this.application = application;
		
		this.layers = new Group[layer];
		for (int i = 0; i < layer; i++) {
			Group group = new Group();
			
			group.setTransform(false);
			
			if (this.trackingCamera != null) {
				group.setCullingArea(this.trackingCamera.getFrame());
			}
			
			layers[i] = group;
			this.addActor(group);
		}
		this.aerialLayer = new Group();
		this.aerialLayer.setTransform(false);
		this.getRoot().setTransform(false);
		
		this.factory = new EntityFactory(this, !application.drawingEnabled);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void removeAllEntites() {
		this.factory.destroyAllEntities();
	}
	
	@Override
	public void draw() {
		if (this.map != null) {
			this.map.drawBackground(this.application.batch);
		}
		
		this.drawWithoutChildren();
		
		if (this.map != null) {
			this.map.drawForeground(this.application.batch);
		}
		
		this.getSpriteBatch().begin();
		this.aerialLayer.draw(this.getSpriteBatch(), 1f);
		this.getSpriteBatch().end();
		
		this.drawChildren();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		this.aerialLayer.act(delta);
	}
	
	public void addActorToAerial(Actor actor) {
		this.aerialLayer.addActor(actor);
	}
	
	public void addDrawable(Actor actor, int layer) {
		this.layers[layer].addActor(actor);
	}

	public void addEntity(Entity entity, int layer) {
		if (this.map != null) {
			this.map.trackActorCollision(entity);
		}
		
		this.layers[layer].addActor(entity);
	}
	
	public boolean removeEntity(Entity entity) {
		if (this.map != null) {
			this.map.untrackActorCollision(entity);
		}
		return entity.remove();
	}
	
	@Override
	public void fireEvent(GameEventCreator event) {
		if (this.eventPropagator != null) {
			this.eventPropagator.fireEvent(event);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final TrackingCamera getTrackingCamera() {
		return this.trackingCamera;
	}
	
	public void setMap(TMXMap map) {
		this.map = map;
		
		if (map != null) {
			map.setCamera(this.trackingCamera);
		}
	}
	
	public final TMXMap getMap() {
		return this.map;
	}
	
	public final int getLayersCount() {
		return this.layers.length;
	}
	
	public final Entity getEntity(int entityID) {
		return this.factory.getEntityHandler().getEntityByID(entityID);
	}
	
	public final EntityFactory getFactory() {
		return this.factory;
	}

	public final IEventPropagator getEventPropagator() {
		return eventPropagator;
	}

	public final void setEventPropagator(IEventPropagator eventPropagator) {
		this.eventPropagator = eventPropagator;
	}

	public final Application getApplication() {
		return this.application;
	}
}
