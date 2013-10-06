/////////////////////////////////////////////////
// Project : killing-sight
// Package : actions
// VelocityAction.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 7, 2012 at 6:59:49 PM
////////

package com.kerious.framework.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kerious.framework.collisions.CollisionHandler;
import com.kerious.framework.collisions.ICollisionable;
import com.kerious.framework.world.entities.Entity;

public class VelocityAction extends Action {

	////////////////////////
	// VARIABLES
	////////////////

	private Entity entity;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public VelocityAction() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public boolean act(float delta) {

		final float entitySpeed = this.entity.getSpeed();
		Vector2 moveDirection = this.entity.getMoveDirection();

		delta *= 1000;
		
		final float offset = entitySpeed * delta;
		final float xOffset = moveDirection.x * offset;
		final float yOffset = moveDirection.y * offset;
		
		final CollisionHandler cHandler = this.entity.getCollisionHandler();

		if (cHandler != null) {

			if (this.entity.canBeCollisioned()) {
				cHandler.removeEntity(this.entity);
			}

			this.actor.translate(xOffset, yOffset);
			ICollisionable collisioned = this.checkCollision();
			if (collisioned != null) {
				this.actor.translate(-xOffset, -yOffset);
				if (this.entity.getCollisionHandler() != null) {
					this.slide(collisioned, xOffset, yOffset);
				}
			}

			if (this.entity.canBeCollisioned()) {
				cHandler.addEntity(this.entity);
			}

		} else {
			this.actor.translate(xOffset, yOffset);
		}

		return false;
	}

	private void slide(ICollisionable collisioned, float xOffset, float yOffset) {
		float effectedXOff = 0;
		float effectedYOff = 0;
		if (xOffset > 0) {
			float diff = collisioned.getX() - (this.actor.getX() + this.actor.getWidth());
			if (diff > 0 && diff < xOffset) {
				this.actor.translate(0, yOffset);
				xOffset = 0;
				effectedYOff = yOffset;
			}
		} else if (xOffset < 0) {
			float diff = collisioned.getX() + collisioned.getWidth() - this.actor.getX();
			if (diff < 0 && diff > xOffset) {
				this.actor.translate(0, yOffset);
				xOffset = 0;
				effectedYOff = yOffset;
			}
		}
		if (yOffset > 0) {
			float diff = collisioned.getY() - (this.actor.getY() + this.actor.getHeight());
			if (diff > 0 && diff < yOffset)
				this.actor.translate(xOffset, 0);
			yOffset = 0;
			effectedXOff = xOffset;
		} else if (yOffset < 0) {
			float diff = collisioned.getY() + collisioned.getHeight() - this.actor.getY();
			if (diff < 0 && diff > yOffset)
				this.actor.translate(xOffset, 0);
			yOffset = 0;
			effectedXOff = xOffset;
		}
		if (this.checkCollision() != null)
			this.actor.translate(-effectedXOff, -effectedYOff);
	}

	private ICollisionable checkCollision() {
		ICollisionable collisioned = this.entity.getCollisionHandler().checkCollisions(this.entity);

		return collisioned;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public void setActor(Actor actor) {
		super.setActor(actor);
		this.entity = (Entity)actor;
	}

}
