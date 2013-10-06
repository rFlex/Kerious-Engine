/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// Server.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 24, 2012 at 4:49:27 PM
////////

package com.kerious.framework.server;

import com.kerious.framework.ApplicationRunner;
import com.kerious.framework.console.Console;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.NetworkGate;
import com.kerious.framework.network.NetworkPeer;
import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.network.protocol.packets.ConnectionPacket;
import com.kerious.framework.utils.EventListenerHolder;
import com.kerious.framework.utils.IEventListener;
import com.kerious.framework.utils.Timer;

public class KeriousServer implements ThreadLoop.LoopIteration {

	////////////////////////
	// VARIABLES
	////////////////

	public final EventListenerHolder<User> onUserConnected = new EventListenerHolder<User>();
	public final EventListenerHolder<User> onUserDisconnected = new EventListenerHolder<User>();
	public final EventListenerHolder<KeriousUDPPacket> onUnmanagedPacketReceived = new EventListenerHolder<KeriousUDPPacket>();
	public final UserManager userManager;
	public final ApplicationRunner applicationRunner;
	protected KeriousServerIntendent intendent;
	protected NetworkGate gate;
	private InputCommandListener inputListener;
	private ThreadLoop loop;
	private Timer fpsCounter;
	private int currentFPSCount;
	private long timeAtStart;
	private int currentTime;
	private float fps;
	private float currentFPS;
	
	////////////////////////
	// NESTED CLASSES
	////////////////

	public static interface KeriousServerIntendent {
		boolean shouldAcceptConnection(ConnectionPacket connectionPacket, NetworkPeer peer);
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousServer(ApplicationRunner applicationRunner) {
		this.applicationRunner = applicationRunner;
		this.userManager = new UserManager(this);
		
		this.fpsCounter = new Timer();
		
		this.setFPS(60);
		this.setIntendent(null);
		
		applicationRunner.setKeriousServer(this);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void start(boolean enableInput) {
		this.start(enableInput, 0);
	}
	
	public void start(boolean enableInput, int listenPort) {
		if (this.loop != null) {
			throw new KeriousException("The server is already started.");
		}
		
		applicationRunner.create();
		
		if (enableInput) {
			final Console console = this.applicationRunner.getApplication().getConsole();
			if (console != null) {
				this.inputListener = new InputCommandListener(console);
			} else {
				throw new KeriousException("Cannot enable the input if no console is set.");
			}
		}
		
		this.gate = this.applicationRunner.getApplication().networkManager.openGate(listenPort);
		
		if (gate == null) {
			throw new KeriousException("Unable to create server due to a network problem");
		}

		this.gate.onPacketArrived.addListener(new PacketHandler(this));
		
		this.loop = new ThreadLoop("Kerious Server", 0, this);
		this.loop.onThreadStarted.addListener(new IEventListener<ThreadLoop>() {
			
			@Override
			public void onFired(Object sender, ThreadLoop arg) {
				onStarted();
			}
		});
		
		this.updateLoopRate();
		this.loop.start();
		
		if (enableInput) {
			this.inputListener.start();
		}
	}

	protected void onStarted() {
		this.timeAtStart = System.currentTimeMillis();
		System.out.println("Kerious Server started.");
		System.out.println("Listening on " + this.getListenPort() + ".");
	}
	
	public void stop() {
		if (this.loop == null) {
			throw new KeriousException("The server is not started.");
		}
		
		this.gate.dispose();
		this.loop.stop();
		if (this.inputListener != null) {
			this.inputListener.stop();
		}
		
		this.loop = null;
	}

	@Override
	public void run(float delta) {
		this.currentTime = (int)(System.currentTimeMillis() - this.timeAtStart);
		
		if (this.fpsCounter.hasElapsed()) {
			this.currentFPS = this.currentFPSCount;
			this.currentFPSCount = 0;
			this.fpsCounter.start(1f);
		}
		
		// Flush network and pending actions
		this.applicationRunner.getApplication().flush();
		// Update user logics depending on the received packets
		this.userManager.updateLogic();
		// Update the world and render it
		this.applicationRunner.getApplication().render(delta);
		this.userManager.sendUpdates();
		this.currentFPSCount++;
	}

	private final void updateLoopRate() {
		if (this.loop != null) {
			final float interval = 1f / this.fps;
			
			this.loop.setInterval(interval);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final void setIntendent(KeriousServerIntendent intendent) {
		if (intendent == null) {
			intendent = new KeriousServerIntendent() {
				
				@Override
				public boolean shouldAcceptConnection(ConnectionPacket connectionPacket, NetworkPeer peer) {
					if (KeriousServer.this.userManager.getConnectedUsers() < KeriousServer.this.userManager.getMaxUsers()) {
						return true;
					}
					return false;
				}
			};
		}
		
		this.intendent = intendent;
	}
	
	public final float getCurrentFPS() {
		return this.currentFPS;
	}
	
	public final float getFPS() {
		return this.fps;
	}
	
	public final void setFPS(float fps) {
		if (fps < 1) {
			throw new KeriousException("The server must be rendered at least at 1 fps.");
		}
		
		this.fps = fps;
		
		this.updateLoopRate();
	}
	
	public final NetworkGate getGate() {
		return this.gate;
	}
	
	public final boolean isStarted() {
		return this.loop != null;
	}
	
	public final int getListenPort() {
		return this.gate != null ? this.gate.getPort() : 0;
	}
	
	public final Console getConsole() {
		return this.applicationRunner.getApplication().getConsole();
	}
	
	public void setConsole(Console console) {
		this.applicationRunner.getApplication().setConsole(console);
	}

	public final int getCurrentTime() {
		return currentTime;
	}
}
