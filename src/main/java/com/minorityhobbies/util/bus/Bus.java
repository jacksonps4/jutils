package com.minorityhobbies.util.bus;

/**
 * General purpose message bus. Allows for the creation of messages and standard
 * subscriptions as well as the publication of messages.
 * 
 * Not safe for use by concurrent threads.
 * 
 */
public interface Bus {
	BusMessageBuilder getBusMessageBuilder();

	BusMessageSubscriptions getSubscriptions();

	void publish(BusMessage msg);

	BusMessageSubscriptionHandle subscribe(BusMessageSubscription subscription,
			BusMessageHandler handler);

	BusServer getBusServer();
}
