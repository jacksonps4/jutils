package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class BusServerFactory {
	private List<URI> remoteEndpoints;
	private Integer localPort;

	public BusServerFactory() {
		this.remoteEndpoints = new LinkedList<URI>();
	}

	public BusServerFactory listenOn(int port) {
		this.localPort = new Integer(port);
		return this;
	}

	public BusServerFactory connectTo(String hostname, int port) {
		try {
			this.remoteEndpoints.add(new URI("socket://" + hostname + ":"
					+ port));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public Bus build() throws IOException {
		StandardBus bus = new StandardBus();
		BusServer server = bus.getBusServer();
		if (localPort != null) {
			server.addConnection(new StandardSocketServerBusMessageConnection(localPort));
		}
		for (URI remoteEndpoint : remoteEndpoints) {
			server.addConnection(new StandardSocketBusMessageConnection(remoteEndpoint));
		}
		return bus;
	}
}
