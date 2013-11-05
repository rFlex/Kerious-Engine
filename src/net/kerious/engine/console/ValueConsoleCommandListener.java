/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// ValueConsoleCommandListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 3, 2013 at 4:18:16 PM
////////

package net.kerious.engine.console;

public interface ValueConsoleCommandListener<T> {
	
	void onValueChanged(ValueConsoleCommand<T> valueConsoleCommand);

}
