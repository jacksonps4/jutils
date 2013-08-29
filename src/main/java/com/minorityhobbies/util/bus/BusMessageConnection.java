package com.minorityhobbies.util.bus;

import java.io.Closeable;
import java.io.IOException;

public interface BusMessageConnection extends Closeable {
	void start() throws IOException;
	
	void push(BusMessage msg) throws IOException;

	BusMessageSubscriptionHandle pull(BusMessageSubscription subscription,
			BusMessageHandler handler);
}
