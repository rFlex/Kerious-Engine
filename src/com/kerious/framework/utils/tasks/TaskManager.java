/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.server.utils
// TaskManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 9, 2012 at 9:36:24 AM
////////

package com.kerious.framework.utils.tasks;

import java.util.Iterator;
import java.util.LinkedList;

public class TaskManager {

	////////////////////////
	// VARIABLES
	////////////////

	private LinkedList<Task> _tasks;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TaskManager() {
		this._tasks = new LinkedList<Task>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void flush() {
		if (!this._tasks.isEmpty()) {
			Iterator<Task> it = this._tasks.iterator();
			
			while (it.hasNext()) {
				Task task = it.next();
				
				task.update();
				if (task.hasElapsed()) {
					it.remove();
				}
			}
		}
	}
	
	public void addTask(Task task) {
		this._tasks.add(task);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
