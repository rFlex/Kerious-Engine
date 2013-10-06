package com.kerious.framework.collisions;

import java.util.LinkedList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kerious.framework.utils.GeometryUtils;
import com.kerious.framework.utils.IRectangle;

public class GraphNode extends Rectangle implements IRectangle {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 1L;
	private Vector2 center;
	private Rectangle babyRectangle; // lol
	private LinkedList<GraphNode> neighbours;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public GraphNode(Rectangle rectangle) {
		this.center = new Vector2();
		this.set(rectangle);
		this.neighbours = new LinkedList<GraphNode>();
		this.setBabyRectangle(new Rectangle(rectangle.getX() + rectangle.getWidth() / 2 - 8, rectangle.getY() + rectangle.getHeight() / 2 - 8, 16, 16));
	}

	////////////////////////
	// METHODS
	////////////////

	public float getDistance(GraphNode otherNode) {
		return (GeometryUtils.greedyEuclidean(this.center.x, otherNode.center.x, this.center.y, otherNode.center.y));
//		return (GeometryUtils.greedyEuclidean(this.rectangle.getX() + this.rectangle.getWidth() / 2,
//											otherNode.getRectangle().getX() + otherNode.getRectangle().getWidth() / 2,
//											this.rectangle.getY() + this.rectangle.getHeight() / 2,
//											otherNode.getRectangle().getY() + otherNode.getRectangle().getHeight() / 2));
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public void set(Rectangle rectangle) {
		super.set(rectangle);
		this.center.x = rectangle.getX() + rectangle.getWidth() / 2;
		this.center.y = rectangle.getY() + rectangle.getHeight() / 2;
	}

	public void addNeighbour(GraphNode node) {
		this.neighbours.addLast(node);
	}

	public LinkedList<GraphNode> getNeighbours() {
		return this.neighbours;
	}

	public Rectangle getBabyRectangle() {
		return babyRectangle;
	}

	public void setBabyRectangle(Rectangle babyRectangle) {
		this.babyRectangle = babyRectangle;
	}
	
	public Vector2 getCenter() {
		return this.center;
	}
}
