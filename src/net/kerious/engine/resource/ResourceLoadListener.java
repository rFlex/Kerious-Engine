/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceLoadListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:16:35 AM
////////

package net.kerious.engine.resource;

public interface ResourceLoadListener<T> {
	
	/**
	 * Called when the ResourceManager finished the gather all the mandatory associated resources
	 * with this resource
	 * @param resourceManager
	 * @param resource
	 */
	void onFinishedCompileDependencies(ResourceManager resourceManager, Resource<T> resource);
	
	/**
	 * Called when the ResourceManager has started to load one of the dependency
	 * @param resourceManager
	 * @param resource
	 * @param loadingDependency
	 */
	void onStartedLoadingDependency(ResourceManager resourceManager, Resource<T> resource, Resource<?> loadingDependency);
	
	/**
	 * Called when the ResourceManager has finished to load one of the dependecy
	 * @param resourceManager
	 * @param resource
	 * @param loadedDependency
	 */
	void onFinishedLoadingDependency(ResourceManager resourceManager, Resource<T> resource, Resource<?> loadedDependency);
	
	/**
	 * Called when the ResourceManager has successfully loaded the resource and all its dependencies
	 * @param resourceManager
	 * @param resource
	 */
	void onLoaded(ResourceManager resourceManager, Resource<T> resource);
	
	/**
	 * Called when the ResourceManager has failed to load the resource or one of its dependency
	 * @param resourceManager
	 * @param resource
	 * @param exception
	 */
	void onFailedLoading(ResourceManager resourceManager, Resource<T> resource, Throwable exception);
	
}
