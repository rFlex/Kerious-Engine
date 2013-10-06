/////////////////////////////////////////////////
// AStar.hpp created in /Babel/protocol/include/tools/AStar.hpp
//
// Author : Simon CORSIN <corsin_s@epitech.net>
// File created on May 10, 2012 at 4:00:27 PM
////////

package com.kerious.framework.utils.path;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

public class AStar<T> {
	
	////////////////////////
	// VARIABLES
	////////////////

	private INodeHandle<T> astarHandle;
	private TreeSet<ANode<T>> sortedOpenList;
	private Map<T, ANode<T>> openList;
	private Map<T, ANode<T>> closedList;
	private ArrayList<T> neighborsList;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AStar(INodeHandle<T> compareFunc) {
		this.astarHandle = compareFunc;
		
		this.neighborsList = new ArrayList<T>();
		this.sortedOpenList = new TreeSet<ANode<T>>(new Comparator<ANode<T>>() {

			@Override
			public int compare(ANode<T> o1, ANode<T> o2) {
				return o1.getFScore() - o2.getFScore();
			}
		});
		
		this.openList = new HashMap<T, ANode<T>>();
		this.closedList = new HashMap<T, ANode<T>>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void cleanSolver() {
		this.openList.clear();
		this.closedList.clear();
		this.neighborsList.clear();
		this.sortedOpenList.clear();
	}

	public void updateList(ANode<T> currentNode, ArrayList<T> neighbors, T endNode) {
		for (T it : neighbors) {
			if (isOnClosedList(it)) {
				continue;
			}

			ANode<T> node = getNodeInOpenList(it);

			int GScore = (node != null ? node.getFather().getGScore() : 0) + 1;
			int HScore = astarHandle.computeNodeDistance(it, endNode);
			int FScore = GScore + HScore;
			if (node != null) {
				node.setFather(currentNode);
				node.setGScore(GScore);
				node.setHScore(HScore);
				node.setFScore(FScore);
			} else {
				addToOpenList(new ANode<T>(it, GScore, HScore, FScore, currentNode));
			}
		}
	}

	public boolean solve(T nodeStart, T nodeEnd) {
		return false;
	}

	public boolean solve(T nodeStart, T nodeEnd, LinkedList<T> result) {
		ANode<T> currentNode = null;
		boolean found = false;

		addToOpenList(nodeStart);
		while (!openList.isEmpty()) {
			currentNode = getBestNode();
			if (currentNode.getValue() == nodeEnd) {
				break;
			}

			addToClosedList(currentNode);
			this.neighborsList.clear();
			astarHandle.addConnectedNodes(currentNode.getValue(), this.neighborsList);
			updateList(currentNode, this.neighborsList, nodeEnd);
		}
		if (!openList.isEmpty()) {
			while (currentNode.getValue() != nodeStart) {
				result.addFirst(currentNode.getValue());
				currentNode = currentNode.getFather();
			}
			result.addFirst(currentNode.getValue());
			found = true;
		}
		cleanSolver();
		return found;
	}
	
	protected final void addToOpenList(ANode<T> node) {
		if (this.sortedOpenList.add(node)) {
			this.openList.put(node.getValue(), node);
		}
	}

	protected final void addToClosedList(ANode<T> node) {
		this.openList.remove(node.getValue());
		this.closedList.put(node.getValue(), node);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
//	protected final NodeIterator<T> getBestNode() {
//		NodeIterator<T> bestNode = this.tmpNodeIterator;
//		bestNode.node = null;
//		bestNode.iterator = null;
//		
//		Iterator<ANode<T>> it = this.openListAsLinkedList.iterator();
//		
//		while (it.hasNext()) {
//			ANode<T> currentNode = it.next();
//			if (bestNode.node == null || currentNode.getFScore() < bestNode.node.getFScore()) {
//				bestNode.node = currentNode;
//				bestNode.iterator = it;
//			}
//		}
//		
//		return bestNode;
//	}
	
	protected final ANode<T> getBestNode() {
		ANode<T> bestNode = this.sortedOpenList.pollFirst();
//
//		for (ANode<T> it : this.openList.values()) {
//			if (bestNode == null || it.getFScore() < bestNode.getFScore()) {
//				bestNode = it;
//			}
//		}

		return bestNode;
	}

	protected final ANode<T> getNodeInOpenList(T node) {
		return this.openList.get(node);
	}

	protected void addToOpenList(T node) {
		addToOpenList(new ANode<T>(node));
	}

	protected boolean isOnOpenList(T node) {
		if (getNodeInOpenList(node) != null) {
			return true;
		}
		return false;
	}

	protected boolean isOnClosedList(T node) {
		return this.closedList.get(node) != null;
	}
}
