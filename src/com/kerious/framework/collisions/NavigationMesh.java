package com.kerious.framework.collisions;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.kerious.framework.utils.GeometryUtils;
import com.kerious.framework.utils.GridGraph;
import com.kerious.framework.utils.path.INodeHandle;

public class NavigationMesh implements INodeHandle<GraphNode>{

	////////////////////////
	// VARIABLES
	////////////////
	
	private ArrayList<GraphNode> graphNodes;
	private GridGraph<GraphNode> graphNode;
	private TiledMap tiledMap;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public NavigationMesh(TiledMap tiledMap, CollisionHandler _collisionHandler) {
		this.tiledMap = tiledMap;
		this.graphNode = new GridGraph<GraphNode>(64);
		this.graphNodes = new ArrayList<GraphNode>();
		for (int x = 0; x < tiledMap.width; x += 4)
			for (int y = 0; y < tiledMap.height; y += 4)
				this.generateMesh(_collisionHandler, x, y, 4, 4);
	}

	////////////////////////
	// METHODS
	////////////////

	private void generateMesh(CollisionHandler _cHndlr, int x, int y, int w, int h) {
		if (w == 0 || h == 0)
			return ;
		for (int x1 = x; x1 < x + w; x1++) {
			for (int y1 = y; y1 < y + h; y1++) {
				if (_cHndlr.hasCollisionableAtPosition(x1, y1)) {
					generateMesh(_cHndlr, x, y, x1 - x, y1 - y);
					generateMesh(_cHndlr, x, y1, x1 - x, 1);
					generateMesh(_cHndlr, x, y1 + 1, x1 - x, h - (y1 - y + 1));
					generateMesh(_cHndlr, x1, y1 + 1, 1, h - (y1 - y + 1));
					generateMesh(_cHndlr, x1 + 1, y1 + 1, w - (x1 - x + 1), h - (y1 - y + 1));
					generateMesh(_cHndlr, x1 + 1, y1, w - (x1 - x + 1), 1);
					generateMesh(_cHndlr, x1 + 1, y, w - (x1 - x + 1), y1 - y);
					generateMesh(_cHndlr, x1, y, 1, y1 - y);
					return ;
				}
			}
		}
		if (x < 80 && y < 80 && !_cHndlr.hasCollisionableAtPosition(x, y)) {
			GraphNode node = new GraphNode(new Rectangle(x * this.tiledMap.tileWidth - 1, y * this.tiledMap.tileHeight - 1, w * this.tiledMap.tileWidth + 2, h * this.tiledMap.tileHeight + 2));
			this.connectNeighbors(node);
			
			this.getGraphNodes().add(node);
			this.graphNode.addRegion(node);
		}
	}

	private void connectNeighbors(GraphNode node) {
		for (GraphNode otherNode : getGraphNodes()) {
			if (node.overlaps(otherNode)) {
				Rectangle common = GeometryUtils.commonRectangle(node, otherNode);
				if (common.getWidth() > 64.0f || common.getHeight() > 64.0f) {
					node.addNeighbour(otherNode);
					otherNode.addNeighbour(node);
				}
			}
		}
	}

	@Override
	public int computeNodeDistance(GraphNode node, GraphNode endNode) {
		float res = node.getDistance(endNode);
		return (int) res;
	}

	@Override
	public void addConnectedNodes(GraphNode node, ArrayList<GraphNode> neighbors) {
		for (GraphNode neighbour : node.getNeighbours()) {
			neighbors.add(neighbour);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public ArrayList<GraphNode> getGraphNodes() {
		return graphNodes;
	}

	public GraphNode getContainingNode(float x, float y) {
		GraphNode closest = this.graphNode.getNodeForPosition(x, y);
		
		if (closest != null) {
			return closest;
		}
		
		float currEuclidean = 0.0f;
		float closestEuclidean = 0.0f;

		for (GraphNode node : getGraphNodes()) {
//			if (node.contains(x, y)) {
//				return node;
//			}
			currEuclidean = GeometryUtils.greedyEuclidean(x, node.x, y, node.y);
			if (closest == null || currEuclidean < closestEuclidean) {
				closestEuclidean = currEuclidean;
				closest = node;
			}
		}
		
		return closest;
	}
}
