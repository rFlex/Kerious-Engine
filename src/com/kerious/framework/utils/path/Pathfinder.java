package com.kerious.framework.utils.path;

import java.util.LinkedList;

import com.kerious.framework.collisions.GraphNode;
import com.kerious.framework.collisions.NavigationMesh;
import com.kerious.framework.world.entities.Entity;

public class Pathfinder {

	////////////////////////
	// VARIABLES
	////////////////
	
	private NavigationMesh navigationMesh;
	private AStar<GraphNode> aStar;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Pathfinder(NavigationMesh navigationMesh) {
		this.navigationMesh = navigationMesh;
		this.aStar = new AStar<GraphNode>(this.navigationMesh);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public LinkedList<GraphNode> computePath(Entity entity, int x1, int y1, int x2, int y2) {
		GraphNode src, dest;
		LinkedList<GraphNode> result = new LinkedList<GraphNode>();

		src = this.navigationMesh.getContainingNode(x1, y1);
		dest = this.navigationMesh.getContainingNode(x2, y2);
		this.aStar.solve(src, dest, result);
		return result;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
