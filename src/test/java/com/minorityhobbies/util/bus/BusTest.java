package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BusTest {
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		URI discoveryUri = new URI("multicast://239.1.1.100:50100");
		
		Bus b1 = new BusServerFactory().listenOn(8800).discoverable("bus-testservice-one", discoveryUri).build();
		BusMessageBuilder msgs = b1.getBusMessageBuilder();
		
		b1.subscribe(b1.getSubscriptions().newAllMessagesSubscription(), new BusMessageHandler() {
			@Override
			public void onMessage(BusMessage msg) {
				System.out.println("Received: " + msg.getAttributes());
			}
		});
		b1.getBusServer().start();
		
		Bus b2 = new BusServerFactory().discoverAndConnect("bus-testservice-one", discoveryUri).build();
		b2.getBusServer().start();
		
		//Thread.sleep(1000L);
		
		BusMessage msg = msgs.setMessageId("1").setMessageSource("here").setMessageDestination("there").build();
		b2.publish(msg);
		
		Thread.sleep(2000L);
		
		b1.getBusServer().close();
		b2.getBusServer().close();
	}
}
