package com.minorityhobbies.util.bus;

import java.io.IOException;

public class BusTest {
	public static void main(String[] args) throws IOException {
		Bus b1 = new BusServerFactory().listenOn(8800).build();
		BusMessageBuilder msgs = b1.getBusMessageBuilder();
		
		b1.subscribe(b1.getSubscriptions().newAllMessagesSubscription(), new BusMessageHandler() {
			@Override
			public void onMessage(BusMessage msg) {
				System.out.println("Received: " + msg.getAttributes());
			}
		});
		b1.getBusServer().start();
		
		Bus b2 = new BusServerFactory().connectTo("localhost", 8800).build();
		b2.getBusServer().start();
		
		BusMessage msg = msgs.setMessageId("1").setMessageSource("here").setMessageDestination("there").build();
		b2.publish(msg);
	}
}
