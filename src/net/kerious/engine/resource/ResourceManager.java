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
import me.corsin.javatools.task.Task;
import me.corsin.javatools.task.Task.TaskListener;
import me.corsin.javatools.task.TaskQueue;
import net.kerious.engine.KeriousEngine;
import net.kerious.engine.KeriousException;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
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
		
		this.initializeDefaultLoaders();
	}

	////////////////////////
	// METHODS
	////////////////
	
	private void initializeDefaultLoaders() {
		this.addLoader(new TextureLoader(), Texture.class);
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
	
	public <T> Resource<T> getResource(String fileName) {
		synchronized (this.resources) {
			return this.resources.get(fileName);
		}
	}
	
	private <T> Resource<T> createResource(String fileName, Class<T> resourceType) {
		Resource<T> resource = new Resource<T>();
		
		resource.fileName = fileName;
		resource.resourceType = resourceType;
		
		return resource;
	}
	
	private <T> T load(TaskQueue taskQueue, String fileName, Class<T> fileClassType) {
		Resource<T> resource = this.getResource(fileName);
		
		if (resource != null) {
			resource.retain();
		} else {
			ResourceLoader<T> loader = this.getLoader(fileClassType);
			resource = this.createResource(fileName, fileClassType);
			resource.resource = loader.load(taskQueue, this.engine.getFileHandleResolver().resolve(fileName));
			
			synchronized (this.resources) {
				this.resources.put(fileName, resource);
			}
		}
		
		return resource.resource;
	}
	
	public void unload(ResourceBundle bundle) {
		for (ResourceInfo resourceInfo : bundle.getResources()) {
			if (resourceInfo.loaded) {
				resourceInfo.loaded = false;
				this.unload(resourceInfo.fileName);
			}
		}
	}
	
	public void unload(String fileName) {
		Resource resource = this.getResource(fileName);
		
		if (resource != null) {
			resource.release();
			if (resource.getRetainCount() == 0) {
				synchronized (this.resources) {
					this.resources.remove(fileName);
				}
			}
		}
	}

	public void load(ResourceBundle bundle) {
		for (ResourceInfo resource : bundle.getResources()) {
			this.load(resource.fileName, resource.resourceType);
			resource.loaded = true;
		}
	}
	
	public <T> T load(String fileName, Class<T> fileClassType) {
		return this.load(null, fileName, fileClassType);
	}
	
	private void updateResourceBundleProgress(ResourceBundle bundle, int index, ResourceBundleListener listener) {
		if (listener != null) {
			final float progressRatio = (float)index / (float)bundle.getResources().size;
			listener.onLoadingProgressChanged(this, bundle, progressRatio);
		}
	}
	
	private void loadItem(int index, ResourceBundle bundle, ResourceBundleListener listener) {
		final int finalIndex = index;
		final ResourceBundle finalBundle = bundle;
		final ResourceBundleListener finalListener = listener;
		this.updateResourceBundleProgress(bundle, index, listener);
		
		if (index >= bundle.getResources().size) {
			if (listener != null) {
				listener.onLoaded(this, bundle);
			}
		} else {
			final ResourceInfo resourceInfo = bundle.getResources().get(index);
			this.loadAsync(resourceInfo.fileName, resourceInfo.resourceType, new ResourceLoadListener<Object>() {
				@Override
				public void onLoaded(ResourceManager resourceManager,
						String fileName, Object resource) {
					resourceInfo.loaded = true;
					if (finalListener != null) {
						finalListener.onLoadedItem(ResourceManager.this, finalBundle, fileName);
					}
					loadItem(finalIndex + 1, finalBundle, finalListener);
				}

				@Override
				public void onLoadingFailed(ResourceManager resourceManager,
						String fileName, Throwable exception) {
					if (finalListener != null) {
						finalListener.onLoadingFailed(ResourceManager.this, finalBundle, exception);
					}
				}
				
			});
		}
	}
	
	public void loadAsync(ResourceBundle bundle, ResourceBundleListener listener) {
		if (bundle == null) {
			throw new IllegalArgumentException("bundle may not be null");
		}
		
		this.loadItem(0, bundle, listener);
	}
	
	public <T> void loadAsync(String fileName, Class<T> fileClassType, ResourceLoadListener<T> listener) {
		final String finalFileName = fileName;
		final Class<T> finalFileClassType = fileClassType;
		final ResourceLoadListener finalListener = listener;
		
		Task<T> resourceLoadTask = new Task<T>() {
			@Override
			protected T perform() throws Throwable {
				return load(engine.getTaskQueue(), finalFileName, finalFileClassType);
			}
		};
		resourceLoadTask.setListener(new TaskListener<T>() {
			@Override
			public void onCompleted(Object taskCreator, final Task<T> task) {
				if (finalListener != null) {
					engine.getTaskQueue().executeAsync(new Runnable() {
						@Override
						public void run() {
							if (task.getThrownException() == null) {
								finalListener.onLoaded(ResourceManager.this, finalFileName, task.getReturnedValue());
							} else {
								finalListener.onLoadingFailed(ResourceManager.this, finalFileName, task.getThrownException());
							}
						}
					});
				}
			}
		});
		this.loadTaskQueue.executeAsync(resourceLoadTask);
	}
	
	public <T> T tryGet(String fileName) {
		Resource<T> resource;
		synchronized (this.resources) {
			resource = this.resources.get(fileName);
		}

		if (resource == null) {
			return null;
		}
		return resource.resource;
	}
	
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

}
