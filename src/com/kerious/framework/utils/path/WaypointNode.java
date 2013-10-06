package com.kerious.framework.utils.path;

import com.kerious.framework.collisions.ICollisionable;


public class WaypointNode {
	/*
	 * VARIABLES
	 */
	private static double pathID = 0;
	private int coutG;
	private int coutH;
	private int coutF;
	protected WaypointNode father;
	protected boolean walkable;
	protected boolean isOnOpenList;
	protected boolean isOnClosedList;
	protected double knownPathID;
	public float graphX;
	public float graphY;
	public int x;
	public int y;
	
	/*
	 * CONSTRUCTORS
	 */
	public WaypointNode(WaypointNode father, int x, int y, boolean walkable) {
		coutG = 0;
		coutH = 0;
		setCoutF(0);
		this.x = x;
		this.y = y;
		graphX = x * 64;
		graphY = y * 64;
		if (father == null) {
			this.father = this;
		} else {
			this.father = father;
		}
		this.walkable = walkable;
		isOnOpenList = false;
		isOnClosedList = false;
	}
	/*
	 * METHODS
	 */
	protected double computeDistance(float x, float y, float destinationX, float destinationY){
		return Math.sqrt(Math.pow((destinationX - x), 2) + Math.pow(destinationY - y, 2));
	}
	
	public void setFather(WaypointNode father){
		if (father == null){
			this.father = this;
		} else{
			this.father = father;		
		}
	}

	public static void clearWaypointNode(){
		pathID++;
	}
	
	/*
	 * GETTERS
	 */
	public int getCoutF(){
		return coutF;
	}
	public int getCoutH(){
		return coutH;
	}
	public int getCoutG(){
		return coutG;
	}
	public boolean isWalkable(){
		return walkable;
	}
	
	public boolean isWalkable(ICollisionable object, boolean useCManager) {
		if (walkable) {
			return false;
//			return cManager.isFree(object, x, y);
		} else {
			return false;
		}
	}
	public WaypointNode getFather(){
		return father;
	}

	public boolean isOnOpenList(){
		checkID();
		return isOnOpenList;
	}
	public boolean isOnClosedList(){
		checkID();
		return isOnClosedList;
	}
	
	protected void checkID(){
		if (knownPathID != pathID){
			isOnOpenList = false;
			isOnClosedList = false;
			knownPathID = pathID;
		}
	}
	/*
	 * SETTERS
	 */
	
	public void setCoutF(int coutF){
		this.coutF = coutF;
	}
	public void setCoutH(int coutH){
		this.coutH = coutH;
	}
	public void setCoutG(int coutG){
		this.coutG = coutG;
	}
	public void setOnOpenList(){
		checkID();
		isOnOpenList = true;
	}
	public void setOnClosedList(){
		isOnOpenList = false;
		isOnClosedList = true;
	}
	
}
