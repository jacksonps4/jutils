package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class StandardBusServerMulticastDiscovery implements Callable<Void>,
		BusMessageConnection {
	private final ExecutorService executor;
	private final StandardBusMessageSerialiser serialiser;
	private final String serviceName;
	private final URI serviceUri;
	private final URI discoveryAddress;
	private InetAddress discoveryAddr;
	private MulticastSocket socket;

	public StandardBusServerMulticastDiscovery(String serviceName,
			URI serviceUri, URI discoveryAddress) {
		super();
		this.executor = Executors.newSingleThreadExecutor();
		this.serialiser = new StandardBusMessageSerialiser();
		this.serviceName = serviceName;
		this.discoveryAddress = discoveryAddress;
		this.serviceUri = serviceUri;
	}

	public void start() throws IOException {
		socket = new MulticastSocket(discoveryAddress.getPort());
		discoveryAddr = InetAddress.getByName(discoveryAddress.getHost());
		socket.joinGroup(discoveryAddr);

		executor.submit(this);
	}

	@Override
	public Void call() throws Exception {
		try {			
			StandardBusMessageBuilder mb = new StandardBusMessageBuilder();
			mb.setMessageType("DISCOVERY");
			mb.setMessageSource(serviceName);
			mb.setAttribute("address", serviceUri.toString());
			BusMessage msg = mb.build();

			while (!Thread.currentThread().isInterrupted()) {
				byte[] m = serialiser.toBytes(msg);
				DatagramPacket p = new DatagramPacket(m, m.length, discoveryAddr, socket.getLocalPort());
				socket.send(p);
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void close() throws IOException {
		if (executor != null) {
			executor.shutdownNow();
		}
		if (socket != null) {
			socket.close();
		}
	}

	@Override
	public void push(BusMessage msg) throws IOException {
		// discovery server so doesn't do anything with messages
	}

	@Override
	public BusMessageSubscriptionHandle pull(
			BusMessageSubscription subscription, BusMessageHandler handler) {
		return new BusMessageSubscriptionHandle() {
			@Override
			public void close() throws IOException {
			}
		};
	}
}
