package com.minorityhobbies.util.bus;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLSocketFactory;

class StandardSocketBusMessageConnection implements BusMessageConnection {
	private final Map<BusMessageSubscription, BusMessageHandler> subscriptions = new ConcurrentHashMap<BusMessageSubscription, BusMessageHandler>();
	private final ExecutorService executor;
	private final List<Closeable> closeHooks;
	private final BusMessageSerialiser serialiser;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private URI uri;
	
	public StandardSocketBusMessageConnection(URI uri, BusMessageSerialiser serialiser) throws IOException {
		this.uri = uri;
		this.executor = Executors.newSingleThreadExecutor();
		this.closeHooks = new LinkedList<Closeable>();
		this.serialiser = serialiser;
	}

	public StandardSocketBusMessageConnection(String hostname, int port, BusMessageSerialiser serialiser)
			throws IOException, URISyntaxException {
		this(new URI(String.format("socket://%s:%d", hostname, port)), serialiser);
	}

	@Override
	public void start() throws IOException {
		if ("socket".equals(uri.getScheme())) {
			this.socket = new Socket(uri.getHost(), uri.getPort());
		} else if ("ssl".equals(uri.getScheme())) {
			this.socket = SSLSocketFactory.getDefault().createSocket(
					uri.getHost(), uri.getPort());
		}
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
		for (Closeable c : closeHooks) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor.shutdownNow();
	}

	public void addCloseHook(Closeable c) {
		closeHooks.add(c);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandardSocketBusMessageConnection other = (StandardSocketBusMessageConnection) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
