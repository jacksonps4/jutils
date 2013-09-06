package com.minorityhobbies.util.bus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocketFactory;

class StandardSocketServerBusMessageConnection implements BusMessageConnection {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final URI localServiceUri;
	private final ServerSocket server;
	private final ExecutorService executor;
	private final BusMessageSerialiser serialiser = new StandardBusMessageSerialiser();
	private final Map<Socket, Socket> connections = new ConcurrentHashMap<Socket, Socket>();
	private final Map<BusMessageSubscription, BusMessageHandler> subscriptions = new ConcurrentHashMap<BusMessageSubscription, BusMessageHandler>();

	public StandardSocketServerBusMessageConnection(int port)
			throws IOException {
		super();
		this.executor = Executors.newCachedThreadPool();
		try {
			this.localServiceUri = new URI(String.format("socket://0.0.0.0:%d",
					port));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		this.server = new ServerSocket();
	}

	public StandardSocketServerBusMessageConnection(URI localServiceUri)
			throws IOException {
		super();
		this.executor = Executors.newCachedThreadPool();
		this.localServiceUri = localServiceUri;
		if ("socket".equals(localServiceUri.getScheme())) {
			this.server = new ServerSocket();
		} else if ("ssl".equals(localServiceUri.getScheme())) {
			this.server = SSLServerSocketFactory.getDefault()
					.createServerSocket();
		} else {
			throw new IllegalArgumentException("Unsupported URI scheme: "
					+ localServiceUri.getScheme());
		}
	}

	@Override
	public void start() throws IOException {
		this.server.bind(new InetSocketAddress(localServiceUri.getHost(),
				localServiceUri.getPort()));
		logger.info(String.format("Binding service at %s", localServiceUri));
		
		executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				while (!Thread.currentThread().isInterrupted()) {
					final Socket socket = server.accept();
					connections.put(socket, socket);
					executor.submit(new StandardSocketBusMessageConnectionStreamReader(
							socket.getInputStream(), serialiser,
							new StandardSocketBusMessageConnectionPuller(
									subscriptions)));
				}
				return null;
			}
		});
	}

	@Override
	public void close() throws IOException {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.executor.shutdownNow();

		for (Socket connection : connections.keySet()) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void push(BusMessage msg) throws IOException {
		for (Socket connection : connections.keySet()) {
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.write(serialiser.toBytes(msg));
			out.flush();
		}
	}

	@Override
	public BusMessageSubscriptionHandle pull(
			BusMessageSubscription subscription, BusMessageHandler handler) {
		subscriptions.put(subscription, handler);
		return new StandardBusMessageSubscriptionHandle(subscriptions,
				subscription);
	}
}
