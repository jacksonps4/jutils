package com.minorityhobbies.util.bus;

import java.io.Closeable;
import java.io.IOException;

/**
 * A connection to enable a {@link BusServer} to connect to other
 * {@link BusServer} instances.
 * 
 */
public interface BusMessageConnection extends Closeable {
	void start() throws IOException;
	
	void push(BusMessage msg) throws IOException;

	BusMessageSubscriptionHandle pull(BusMessageSubscription subscription,
			BusMessageHandler handler);
}
