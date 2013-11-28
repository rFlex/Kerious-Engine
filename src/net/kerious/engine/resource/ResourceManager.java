/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : 
// ResourceManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 16, 2013 at 10:20:41 PM
////////

package net.kerious.engine.resource;

import java.io.Closeable;

import me.corsin.javatools.task.MultiThreadedTaskQueue;
import me.corsin.javatools.task.TaskQueue;
import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;
import net.kerious.engine.map.GameMap;
import net.kerious.engine.map.MapLoader;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ResourceManager implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	public static ResourceManager sharedResourceManager;
	
	final private KeriousEngine engine;
	final private ObjectMap<String, Resource> resources;
	final private ObjectMap<Class, ResourceLoader> loaders;
	final private TaskQueue loadTaskQueue;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ResourceManager(KeriousEngine engine, FileHandleResolver fileHandleResolver) {
		this.engine = engine;
		this.resources = new ObjectMap<String, Resource>();
		this.loaders = new ObjectMap<Class, ResourceLoader>();
		this.loadTaskQueue = new MultiThreadedTaskQueue(1);
		
		this.initializeBuiltinLoaders();
	}

	////////////////////////
	// METHODS
	////////////////
	
	private void initializeBuiltinLoaders() {
		this.addLoader(new TextureLoader(), Texture.class);
		this.addLoader(new MapLoader(), GameMap.class);
		this.addLoader(new ResourceBundleLoader(), ResourceBundle.class);
	}
	
	private <T> ResourceLoader<T> getLoader(Class<T> fileClassType) {
		ResourceLoader<T> loader;
		synchronized (this.loaders) {
			loader = this.loaders.get(fileClassType);
		}
		
		if (loader == null) {
			throw new KeriousException("No loader set for type " + fileClassType.getSimpleName());
		}
		
		return loader;
	}
	
	public <T> void addLoader(ResourceLoader<T> loader, Class<T> type) {
		this.loaders.put(type, loader);
	}
	
	/**
	 * Return the resource associated with the fileName or null if none
	 * was set
	 * @param fileName
	 * @return
	 */
	public <T> Resource<T> getResource(String fileName) {
		synchronized (this.resources) {
			return this.resources.get(fileName);
		}
	}
	
	final private Resource<?> getResourceOrCreate(ResourceDescriptor resourceDescriptor) {
		String fileName = resourceDescriptor.getFileName();
		
		synchronized (this.resources) {
			Resource<?> resource = this.resources.get(fileName);
			
			if (resource == null) {
				resource = new Resource(resourceDescriptor);
				this.resources.put(fileName, resource);
				
				if (!resourceDescriptor.isFilePathResolved()) {
					resourceDescriptor.resolveFilePath(this.engine.getFileHandleResolver());
				}
			}
			
			return resource;
		}
	}
	
	final private Resource getResourceOrCreate(Class<?> resourceType, String fileName) {
		return this.getResourceOrCreate(new ResourceDescriptor(resourceType, fileName));
	}
	
	private void compileDependencies(final Resource resource, final ResourceLoadListener<?> listener) {
		if (!resource.getResourceDescriptor().isFilePathResolved()) {
			resource.getResourceDescriptor().resolveFilePath(this.engine.getFileHandleResolver());
		}
		
		if (!resource.isDependenciesCompiled()) {
			ResourceLoader<?> loader = this.getLoader(resource.getResourceDescriptor().getResourceType());
			Array<ResourceDescriptor> dependencies = null
					;
			try {
				dependencies = loader.compileDependencies(resource);
			} catch (Exception e) {
				throw new KeriousException("Failed to compile dependencies: " + e.getMessage());
			}
			
			if (dependencies != null) {
				resource.addDependencies(dependencies);
			}
			
			dependencies = resource.getDependencies();
			
			for (ResourceDescriptor dependency : dependencies) {
				Resource<?> dependencyResource = this.getResourceOrCreate(dependency);
				this.compileDependencies(dependencyResource, null);
				resource.setTotalDependenciesCount(resource.getTotalDependenciesCount() + dependencyResource.getTotalDependenciesCount());
			}
		}
		
		if (listener != null) {
			engine.getTaskQueue().executeAsync(new Runnable() {
				public void run() {
					listener.onFinishedCompileDependencies(ResourceManager.this, resource);
				}
			});
		}
	}
	
	private void beginLoad(Resource<?> resource) {
		Array<ResourceDescriptor> dependencies = resource.getDependencies();
		
		for (ResourceDescriptor dependency : dependencies) {
			Resource<?> dependencyResource = this.getResource(dependency.getFileName());
			this.beginLoad(dependencyResource);
		}
		
		this.loadResourceItem(null, resource);
	}
	
	final private void loadResourceItem(TaskQueue taskQueue, Resource resource) {
		if (!resource.getResourceDescriptor().isLoaded()) {
			ResourceLoader<?> loader = this.getLoader(resource.getResourceDescriptor().getResourceType());
			
			if (!loader.needsDrawingContext() || (loader.needsDrawingContext() && this.engine.getRenderer().isDrawingContextAvailable())) {
				Object item = null;
				try {
					item = loader.load(taskQueue, resource, this);
					resource.setResource(item);
				} catch (Exception e) {
					throw new KeriousException("Failed to load item: " + e.getMessage(), e);
				}
				if (item == null) {
					throw new KeriousException("Failed to load item: The loader " + loader.getClass().getSimpleName() + " returned null for " + resource.getResourceDescriptor().getFileName());
				}
			}
			
			resource.getResourceDescriptor().setLoaded(true);
		} else {
			resource.retain();
		}
	}
	
	public <T> void load(Class<T> resourceType, String fileName) {
		Resource<T> resource = this.getResourceOrCreate(resourceType, fileName);
		this.load(resource);
	}
	
	public void unload(String fileName) {
		Resource<?> dependencyResource = this.getResource(fileName);
		if (dependencyResource != null) {
			this.unload(dependencyResource);
		}
	}
	
	public <T> void unload(Resource<T> resource) {
		resource.release();
		
		if (resource.getRetainCount() == 0) {
			resource.getResourceDescriptor().setLoaded(false);
			this.resources.remove(resource.getResourceDescriptor().getFileName());
		}
		
		Array<ResourceDescriptor> dependencies = resource.getDependencies();
		
		for (ResourceDescriptor dependency : dependencies) {
			this.unload(dependency.getFileName());
		}
	}
	
	public <T> void load(Resource<T> resource) {
		this.compileDependencies(resource, null);
		this.beginLoad(resource);
	}
	
	private void beginLoadAsync(final Resource baseResource, final Resource resource, final ResourceLoadListener<?> listener) {
		Array<ResourceDescriptor> dependencies = resource.getDependencies();
		
		for (ResourceDescriptor dependency : dependencies) {
			Resource<?> dependencyResource = this.getResource(dependency.getFileName());
			this.beginLoadAsync(baseResource, dependencyResource, listener);
		}
		
		if (baseResource != resource && listener != null) {
			engine.getTaskQueue().executeAsync(new Runnable() {
				@Override
				public void run() {
					listener.onStartedLoadingDependency(ResourceManager.this, baseResource, resource);
				}
			});
		}
		
		this.loadResourceItem(engine.getTaskQueue(), resource);
		
		if (baseResource != resource && listener != null) {
			engine.getTaskQueue().executeAsync(new Runnable() {
				@Override
				public void run() {
					listener.onFinishedLoadingDependency(ResourceManager.this, baseResource, resource);
				}
			});
		}
	}
	
	public <T> void loadAsync(Resource<T> resource, ResourceLoadListener<T> listener) {
		final Resource<T> finalResource = resource;
		final ResourceLoadListener<T> finalListener = listener;
		
		this.loadTaskQueue.executeAsync(new Runnable() {
			public void run() {
				try {
					compileDependencies(finalResource, finalListener);
					beginLoadAsync(finalResource, finalResource, finalListener);
					if (finalListener != null) {
						engine.getTaskQueue().executeAsync(new Runnable() {
							@Override
							public void run() {
								finalListener.onLoaded(ResourceManager.this, finalResource);
							}
						});
					}
				} catch (final Throwable e) {
					if (finalListener != null) {
						engine.getTaskQueue().executeAsync(new Runnable() {
							public void run() {
								finalListener.onFailedLoading(ResourceManager.this, finalResource, e);
							}
						});
					}
				}
			}
		});
	}
	
	/**
	 * Get the resource with the filename
	 * If the resource doesn't exist, null will returned
	 * @param fileName
	 * @return
	 */
	public <T> T tryGet(String fileName) {
		Resource<T> resource;
		
		synchronized (this.resources) {
			resource = this.resources.get(fileName);
		}

		if (resource == null) {
			return null;
		}
		
		return resource.getResource();
	}
	
	/**
	 * Get the resource with the fileName
	 * If the resource doesn't exist, an exception will be thrown
	 * @param fileName
	 * @return
	 */
	public <T> T get(String fileName) {
		T resource = this.tryGet(fileName);
		
		if (resource == null) {
			throw new KeriousException("Resource " + fileName + " not loaded");
		}
		
		return resource;
	}

	@Override
	public void close() {
		this.loadTaskQueue.close();
		for (Entry<String, Resource> entry : this.resources.entries()) {
			entry.value.dispose();
		}
		this.resources.clear();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public boolean isDrawingContextAvailable() {
		return this.engine.getRenderer().isDrawingContextAvailable();
	}
}
