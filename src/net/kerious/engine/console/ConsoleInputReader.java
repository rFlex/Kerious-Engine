/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// ConsoleInputReader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2013 at 4:17:34 PM
////////

package net.kerious.engine.console;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.KeriousEngine;

public class ConsoleInputReader implements Closeable {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private KeriousEngine engine;
	final private Console console;
	private BufferedReader reader;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ConsoleInputReader(KeriousEngine engine) {
		this(engine, engine.getConsole());
	}
	
	public ConsoleInputReader(KeriousEngine engine, Console console) {
		if (engine == null) {
			throw new NullArgumentException("engine");
		}
		
		this.engine = engine;
		this.console = console;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void beginReadOnStdin() {
		this.beginRead(System.in);
	}
	
	public void beginRead(InputStream stream) {
		this.beginRead(new InputStreamReader(stream));
	}
	
	public void beginRead(Reader reader) {
		this.endRead();
		
		this.reader = new BufferedReader(reader);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				read();
			}
			
		});
		thread.start();
	}
	
	private void read() {
		try {
			BufferedReader reader = this.reader;
			
			if (reader != null) {
				while (true) {
					final String line = reader.readLine();
					
					if (line == null) {
						return;
					}
					
					this.engine.getTaskQueue().executeAsync(new Runnable() {
						public void run() {
							console.processCommandString(line);
						}
					});
				}
			}
		} catch (IOException e) {
			
		}
	}
	
	public void endRead() {
		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException e) {
			}
			this.reader = null;
		}
	}

	@Override
	public void close() {
		this.endRead();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
