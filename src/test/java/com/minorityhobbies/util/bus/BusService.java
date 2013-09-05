package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BusService {
	public static void main(String[] args) throws IOException, URISyntaxException {
		URI discoveryUri = new URI("multicast://239.1.1.100:50100");

		Bus b1 = new BusServerFactory().listenOn(8800)
				.discoverable("bus-testservice-one", discoveryUri).build();

		b1.subscribe(b1.getSubscriptions().newAllMessagesSubscription(),
				new BusMessageHandler() {
					@Override
					public void onMessage(BusMessage msg) {
						System.out.println("Received: " + msg.getAttributes());
					}
				});
		b1.getBusServer().start();
	}
}
