package com.minorityhobbies.util.bus;

import java.util.Map;

final class StandardSocketBusMessageConnectionPuller
		implements BusMessageHandler {
	private final Map<BusMessageSubscription, BusMessageHandler> subscriptions;
	
	public StandardSocketBusMessageConnectionPuller(
			Map<BusMessageSubscription, BusMessageHandler> subscriptions) {
		super();
		this.subscriptions = subscriptions;
	}

	@Override
	public void onMessage(BusMessage msg) {
		for (Map.Entry<BusMessageSubscription, BusMessageHandler> entry : subscriptions.entrySet()) {
			BusMessageSubscription sub = entry.getKey();
			BusMessageHandler handler = entry.getValue();
			if (sub.matches(msg)) {
				handler.onMessage(msg);
			}
		}
	}
}