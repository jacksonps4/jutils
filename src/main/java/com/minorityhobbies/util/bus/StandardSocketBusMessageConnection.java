package com.minorityhobbies.util.bus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class StandardSocketBusMessageConnection implements BusMessageConnection {
	private final StandardBusMessageSerialiser serialiser = new StandardBusMessageSerialiser();
	private final Map<BusMessageSubscription, BusMessageHandler> subscriptions = new ConcurrentHashMap<BusMessageSubscription, BusMessageHandler>();
	private final ExecutorService executor;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private URI uri;

	public StandardSocketBusMessageConnection(URI uri) throws IOException {
		this.uri = uri;
		this.executor = Executors.newSingleThreadExecutor();
	}

	public StandardSocketBusMessageConnection(String hostname, int port)
			throws IOException, URISyntaxException {
		this(new URI(String.format("socket://%s:%d", hostname, port)));
	}

	@Override
	public void start() throws IOException {
		this.socket = new Socket(uri.getHost(), uri.getPort());
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		executor.submit(new StandardSocketBusMessageConnectionStreamReader(in,
				serialiser, new StandardSocketBusMessageConnectionPuller(
						subscriptions)));
	}

	@Override
	public void push(BusMessage msg) throws IOException {
		byte[] data = serialiser.toBytes(msg);
		out.write(data);
	}

	@Override
	public BusMessageSubscriptionHandle pull(
			BusMessageSubscription subscription, BusMessageHandler handler) {
		subscriptions.put(subscription, handler);
		return new StandardBusMessageSubscriptionHandle(subscriptions,
				subscription);
	}

	@Override
	public void close() throws IOException {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor.shutdownNow();
	}
}
