package com.minorityhobbies.util.bus;

import static com.minorityhobbies.util.bus.BusMessageAttribute.MESSAGE_TYPE;
import static com.minorityhobbies.util.bus.BusMessageAttribute.SOURCE;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

class StandardBusServerMulticastDiscoveryClient implements BusMessageConnection {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final ExecutorService executor;
	private final BusServer busServer;
	private final Map<URI, List<String>> remoteServices;
	private final Map<URI, BusMessageConnection> connectedServices;

	public StandardBusServerMulticastDiscoveryClient(BusServer busServer,
			Map<URI, List<String>> remoteServices) {
		super();
		this.executor = Executors.newCachedThreadPool();
		this.busServer = busServer;
		this.remoteServices = remoteServices;
		this.connectedServices = new HashMap<URI, BusMessageConnection>();
	}

	@Override
	public void start() throws IOException {
		for (Map.Entry<URI, List<String>> remoteServiceEntry : remoteServices
				.entrySet()) {
			final URI discoveryAddress = remoteServiceEntry.getKey();
			final List<String> servicesAtThisAddress = remoteServiceEntry
					.getValue();

			final MulticastSocket socket = new MulticastSocket(
					discoveryAddress.getPort());
			socket.setSoTimeout(1000);
			InetAddress discoveryAddr = InetAddress
					.getByName(discoveryAddress.getHost());
			socket.joinGroup(discoveryAddr);

			executor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					try {
						StandardBusMessageSerialiser serialiser = new StandardBusMessageSerialiser();

						byte[] b = new byte[1024];
						while (!Thread.currentThread().isInterrupted()) {
							DatagramPacket p = new DatagramPacket(b, b.length);
							try {
								socket.receive(p);
								byte[] data = Arrays.copyOfRange(b, 0,
										p.getLength());
								BusMessage discoveryMessage = serialiser
										.fromBytes(data);
								String type = MESSAGE_TYPE
										.get(discoveryMessage);
								if ("DISCOVERY".equals(type)) {
									String serviceName = SOURCE
											.get(discoveryMessage);
									if (servicesAtThisAddress
											.contains(serviceName)) {
										String address = discoveryMessage
												.get("address");
										connect(new URI(address));
									}
								}
							} catch (SocketTimeoutException e) {
								// check for interruptiong
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			});
		}
	}

	private void connect(final URI serviceAddress) throws IOException {
		if (!connectedServices.containsKey(serviceAddress)) {
			StandardSocketBusMessageConnection conn = new StandardSocketBusMessageConnection(
					serviceAddress);
			busServer.addConnection(conn);
			logger.info("Discovered service at " + serviceAddress.toString());
			connectedServices.put(serviceAddress, conn);
			conn.addCloseHook(new Closeable() {
				@Override
				public void close() throws IOException {
					connectedServices.remove(serviceAddress);
					logger.info("Disconnected from service at " + serviceAddress.toString());
				}
			});
		}
	}

	@Override
	public void push(BusMessage msg) throws IOException {
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

	@Override
	public void close() throws IOException {
		if (executor != null) {
			executor.shutdownNow();
		}
	}
}
