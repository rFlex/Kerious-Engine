/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// Box2DDebugView.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 28, 2013 at 5:42:44 PM
////////

package net.kerious.engine.view;

import net.kerious.engine.renderer.DrawingContext;
import net.kerious.engine.renderer.Projection;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsDebugView extends View {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Box2DDebugRenderer renderer;
	private World physicsWorld;
	private Matrix4 projectionMatrix;
	private Matrix4 savedProjectionMatrix;
	private float metersPoint;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public PhysicsDebugView(World physicsWorld) {
		this();
		this.setPhysicsWorld(physicsWorld);
	}

	public PhysicsDebugView() {
		this.renderer = new Box2DDebugRenderer(true, true, true, true, true, true);
		this.projectionMatrix = new Matrix4();
		this.savedProjectionMatrix = new Matrix4();
		this.metersPoint = 1f;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void drawView(DrawingContext context, float width, float height, float alpha) {
		if (this.physicsWorld != null) {
			this.savedProjectionMatrix.set(context.getProjectionMatrix());
			this.projectionMatrix.set(this.savedProjectionMatrix);
			this.projectionMatrix.scale(this.metersPoint, this.metersPoint, 1);
			
			context.setProjectionMatrix(this.projectionMatrix);
			
			this.renderer.render(this.physicsWorld, context.getProjectionMatrix());
			
			context.setProjectionMatrix(this.savedProjectionMatrix);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Box2DDebugRenderer getDebugRenderer() {
		return this.renderer;
	}

	public World getPhysicsWorld() {
		return physicsWorld;
	}

	public void setPhysicsWorld(World physicsWorld) {
		this.physicsWorld = physicsWorld;
	}

	public float getMetersPoint() {
		return metersPoint;
	}

	public void setMetersPoint(float metersPoint) {
		this.metersPoint = metersPoint;
	}

}
