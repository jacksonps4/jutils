package com.minorityhobbies.util.bus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardBus implements Bus {
	private final Map<BusMessageSubscription, BusMessageHandler> subscriptions = new ConcurrentHashMap<BusMessageSubscription, BusMessageHandler>();
	private StandardBusServer busServer = new StandardBusServer(this);
	
	@Override
	public BusMessageBuilder getBusMessageBuilder() {
		return new StandardBusMessageBuilder();
	}

	@Override
	public BusMessageSubscriptions getSubscriptions() {
		return new StandardBusMessageSubscriptions();
	}

	@Override
	public void publish(BusMessage msg) {
		for (Map.Entry<BusMessageSubscription, BusMessageHandler> subscriptionEntry : subscriptions
				.entrySet()) {
			BusMessageSubscription subscription = subscriptionEntry.getKey();
			if (subscription.matches(msg)) {
				BusMessageHandler handler = subscriptionEntry.getValue();
				handler.onMessage(msg);
			}
		}
	}

	@Override
	public BusMessageSubscriptionHandle subscribe(
			final BusMessageSubscription subscription, BusMessageHandler handler) {
		subscriptions.put(subscription, handler);
		return new StandardBusMessageSubscriptionHandle(subscriptions, subscription);
	}

	@Override
	public BusServer getBusServer() {
		return busServer;
	}
}
