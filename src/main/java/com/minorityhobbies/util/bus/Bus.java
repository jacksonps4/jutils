package com.minorityhobbies.util.bus;

public interface Bus {
	BusMessageBuilder getBusMessageBuilder();

	BusMessageSubscriptions getSubscriptions();

	void publish(BusMessage msg);

	BusMessageSubscriptionHandle subscribe(BusMessageSubscription subscription,
			BusMessageHandler handler);
	
	BusServer getBusServer();
}
