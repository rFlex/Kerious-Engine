/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.animations
// ChaseCamera.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 9:26:59 PM
////////

package net.kerious.engine.animations;

import net.kerious.engine.entity.Entity;
import net.kerious.engine.view.KView;

import com.badlogic.gdx.math.Vector2;

public class ChaseCamera extends Animation {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Entity chasedEntity;
	private KView chasedView;
	private float offsetX;
	private float offsetY;
	private boolean useCenterForChasedView;
	private Vector2 position;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ChaseCamera() {
		this.position = new Vector2();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		final KView view = this.getView();
		KView chasedView = this.chasedView;
		
		if (chasedView == null && this.chasedEntity != null) {
			chasedView = this.chasedEntity.getView();
		}
		
		if (chasedView != null && view != null) {
			Vector2 position = this.position;
			
			chasedView.getPositionForViewCoordinate(view, position);

			position.x *= -1f;
			position.y *= -1f;
			
			if (this.useCenterForChasedView) {
				position.x -= chasedView.getWidth() / 2f;
				position.y -= chasedView.getHeight() / 2f;
			}
			
			position.x += this.offsetX; 
			position.y += this.offsetY;
			
			view.setPosition(position);
		}
	}

	@Override
	protected void setInitialStateForView(KView view) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KView getChasedView() {
		return chasedView;
	}

	public void setChasedView(KView chasedView) {
		this.chasedView = chasedView;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public boolean isUseCenterForChasedView() {
		return useCenterForChasedView;
	}

	public void setUseCenterForChasedView(boolean useCenterForChasedView) {
		this.useCenterForChasedView = useCenterForChasedView;
	}

	public Entity getChasedEntity() {
		return chasedEntity;
	}

	public void setChasedEntity(Entity chasedEntity) {
		this.chasedEntity = chasedEntity;
	}
}
