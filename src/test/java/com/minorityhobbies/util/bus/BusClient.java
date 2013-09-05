package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BusClient {
	public static void main(String[] args) throws IOException,
			URISyntaxException, InterruptedException {
		URI discoveryUri = new URI("multicast://239.1.1.100:50100");

		Bus b2 = new BusServerFactory().discoverAndConnect(
				"bus-testservice-one", discoveryUri).build();
		BusMessageBuilder msgs = b2.getBusMessageBuilder();

		b2.getBusServer().start();
		Thread.sleep(2000L);

		BusMessage msg = msgs.setMessageId("1").setMessageSource("here")
				.setMessageDestination("there").build();
		b2.publish(msg);


		b2.getBusServer().close();
	}
}
