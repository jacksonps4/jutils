package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.util.Map;

class StandardBusMessageSubscriptionHandle implements
		BusMessageSubscriptionHandle {
	private final Map<BusMessageSubscription, BusMessageHandler> subscriptions;
	private final BusMessageSubscription subscription;
	
	public StandardBusMessageSubscriptionHandle(
			Map<BusMessageSubscription, BusMessageHandler> subscriptions, BusMessageSubscription subscription) {
		super();
		this.subscriptions = subscriptions;
		this.subscription = subscription;
	}

	@Override
	public void close() throws IOException {
		subscriptions.remove(subscription);
	}
}
