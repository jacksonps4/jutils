package com.minorityhobbies.util;

/**
 * Handler for {@link ReliableSocketConnection}. Implementations are notified
 * when a socket connection is established or disconnection and when data is
 * available.
 * 
 */
public interface ReliableConnectionHandler {
	void onConnected();

	void onDisconnected();

	void processData(byte[] b, int offset, int length);
}
