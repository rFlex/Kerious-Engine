package com.kerious.framework.utils.path;

import java.util.ArrayList;
import java.util.LinkedList;


public class OpenList {

	/*
	 * VARIABLES
	 */
	private ArrayList<WaypointNode> openList;
	private LinkedList<Integer> freeSlots;
	private int size;
	
	/*
	 * CONSTRUCTORS
	 */
	public OpenList(int ensureCapacity){
		openList = new ArrayList<WaypointNode>(ensureCapacity);
		freeSlots = new LinkedList<Integer> ();
		size = 0;
	}
	public OpenList(){
		openList = new ArrayList<WaypointNode>();
		freeSlots = new LinkedList<Integer> ();
		size = 0;
	}
	/*
	 * METHODS
	 */
	public int add(WaypointNode node) {
		size++;
		if (freeSlots.size() > 0){
			int position = freeSlots.getFirst();
			openList.set(position, node);
			freeSlots.removeFirst();
			return position;
		} else{
			openList.add(node);
			return openList.size() - 1;
		}
	}
	
	public void remove(int position) {
		size--;
		openList.set(position, null);
		freeSlots.addLast(position);
	}
	
	public WaypointNode getBestNode() {
		WaypointNode bestNode = null;
		
		if (bestNode == null){
			for (int i = 0; i < openList.size(); i++){
				if (openList.get(i) != null){
					if (bestNode == null) bestNode = openList.get(i);
					else if (openList.get(i).getCoutF() < bestNode.getCoutF()){
						bestNode = openList.get(i);
					}
				}
			}
		}
		return bestNode;
	}
	
	public int size() {
		return size;
	}
}
