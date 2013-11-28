/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// Resource.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:04:49 AM
////////

package net.kerious.engine.resource;

import java.io.Closeable;
import java.io.IOException;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.utils.ReferencableImpl;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Resource<T> extends ReferencableImpl {

	////////////////////////
	// VARIABLES
	////////////////

	private ResourceDescriptor resourceDescriptor;
	private T resource;
	private int totalDependenciesCount;
	private boolean dependenciesCompiled;
	private Array<ResourceDescriptor> dependencies;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Resource(Class<T> resourceType, FileHandle fileHandle) {
		this(new ResourceDescriptor(resourceType, fileHandle));
	}
	
	public Resource(Class<T> resourceType, String fileName) {
		this(new ResourceDescriptor(resourceType, fileName));
	}
	
	public Resource(ResourceDescriptor resourceDescriptor) {
		this.dependencies = new Array<ResourceDescriptor>();
		this.resourceDescriptor = resourceDescriptor;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	public void addDependencies(Iterable<ResourceDescriptor> dependencies) {
		for (ResourceDescriptor resourceDescriptor : dependencies) {
			this.addDependency(resourceDescriptor);
		}
	}
	
	public void addDependency(Class<?> resourceType, String fileName) {
		this.addDependency(new ResourceDescriptor(resourceType, fileName));
	}
	
	public void addDependency(Class<?> resourceType, FileHandle fileHandle) {
		this.addDependency(new ResourceDescriptor(resourceType, fileHandle));
	}
	
	public void addDependency(ResourceDescriptor resourceDescriptor) {
		if (resourceDescriptor == null) {
			throw new NullArgumentException("resourceDescriptor");
		}
		
		this.dependencies.add(resourceDescriptor);
		this.totalDependenciesCount++;
	}
	
	public void removeDependency(String fileName) {
		for (int i = 0, length = this.dependencies.size; i < length; i++) {
			ResourceDescriptor dependency = this.dependencies.get(i);
			
			if (dependency.getFileName().equals(fileName)) {
				this.dependencies.removeIndex(i);
				i--;
			}
		}
	}
	
	public void removeDependency(ResourceDescriptor resourceDescriptor) {
		this.dependencies.removeValue(resourceDescriptor, true);
	}
	
	@Override
	public void dispose() {
		T resource = this.resource;
		if (resource != null) {
			if (resource instanceof Disposable) {
				((Disposable)resource).dispose();
			} else if (resource instanceof Closeable) {
				try {
					((Closeable)resource).close();
				} catch (IOException e) {
				}
			}
			this.resource = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == this) {
			return true;
		}
		
		if (otherObject == null) {
			return false;
		}
		if (!(otherObject instanceof Resource)) {
			return false;
		}
		Resource<T> otherRes = (Resource<T>)otherObject;
		
		return this.resourceDescriptor.equals(otherRes.resourceDescriptor);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isDependenciesCompiled() {
		return dependenciesCompiled;
	}

	public void setDependenciesCompiled(boolean dependenciesCompiled) {
		this.dependenciesCompiled = dependenciesCompiled;
	}

	public int getTotalDependenciesCount() {
		return totalDependenciesCount;
	}

	public void setTotalDependenciesCount(int totalDependenciesCount) {
		this.totalDependenciesCount = totalDependenciesCount;
	}

	public ResourceDescriptor getResourceDescriptor() {
		return resourceDescriptor;
	}

	public Array<ResourceDescriptor> getDependencies() {
		return dependencies;
	}
	
	public T getResource() {
		return resource;
	}

	public void setResource(T resource) {
		this.resource = resource;
	}
}
