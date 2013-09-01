package com.minorityhobbies.util.bus;

import java.io.Closeable;
import java.io.IOException;

/**
 * Used for interfacing a {@link Bus} to other processes on this or other hosts.
 * Safe for use by concurrent threads.
 * 
 */
public interface BusServer extends Closeable {
	void addConnection(BusMessageConnection connection) throws IOException;
	void removeConnection(BusMessageConnection connection) throws IOException;
	
	void start() throws IOException;
}