package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BusServerFactory {
	private List<URI> remoteEndpoints;
	private Map<URI, List<String>> remoteServices;
	private URI localServiceUri;
	private String serviceName;
	private URI discoveryAddress;

	public BusServerFactory() {
		this.remoteEndpoints = new LinkedList<URI>();
		this.remoteServices = new HashMap<URI, List<String>>();
	}

	public BusServerFactory listenOn(URI localServiceUri) {
		this.localServiceUri = localServiceUri;
		return this;
	}

	public BusServerFactory listenOn(int port) {
		try {
			String address = InetAddress.getLocalHost().getHostAddress();
			this.localServiceUri = new URI(String.format("socket://%s:%d",
					address, port));
			return this;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
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

	public BusServerFactory connectToSSL(String hostname, int port) {
		try {
			this.remoteEndpoints.add(new URI("ssl://" + hostname + ":" + port));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public BusServerFactory discoverable(String serviceName,
			URI discoveryAddress) {
		this.serviceName = serviceName;
		this.discoveryAddress = discoveryAddress;
		return this;
	}

	public BusServerFactory discoverAndConnect(String serviceName,
			URI discoveryAddress) {
		List<String> services = remoteServices.get(discoveryAddress);
		if (services == null) {
			services = new LinkedList<String>();
			remoteServices.put(discoveryAddress, services);
		}
		services.add(serviceName);
		
		return this;
	}

	public Bus build() throws IOException {
		StandardBus bus = new StandardBus();
		BusServer server = bus.getBusServer();
		if (localServiceUri != null) {
			server.addConnection(new StandardSocketServerBusMessageConnection(
					localServiceUri));

			// is it discoverable?
			if (serviceName != null) {
				StandardBusServerMulticastDiscovery discovery;
				discovery = new StandardBusServerMulticastDiscovery(serviceName, 
						localServiceUri, discoveryAddress);
				server.addConnection(discovery);
			}
		}
		
		for (URI remoteEndpoint : remoteEndpoints) {
			server.addConnection(new StandardSocketBusMessageConnection(
					remoteEndpoint));
		}

		if (remoteServices != null && remoteServices.size() > 0) {
			StandardBusServerMulticastDiscoveryClient discoveryClient = new StandardBusServerMulticastDiscoveryClient(server, 
					remoteServices);
			server.addConnection(discoveryClient);
		}

		return bus;
	}
}
