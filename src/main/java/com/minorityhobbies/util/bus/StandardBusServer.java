package com.minorityhobbies.util.bus;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class StandardBusServer implements BusServer, Closeable {
	private final Bus internalBus;
	private final BusMessageSubscriptions subscriptions;
	private final List<BusMessageConnection> connections;

	public StandardBusServer(Bus internalBus) {
		connections = new LinkedList<BusMessageConnection>();

		this.internalBus = internalBus;

		subscriptions = internalBus.getSubscriptions();
	}

	@Override
	public void addConnection(BusMessageConnection connection) {
		connections.add(connection);
	}

	@Override
	public void start() throws IOException {
		internalBus.subscribe(subscriptions.newAllMessagesSubscription(),
				new BusMessageHandler() {
					@Override
					public void onMessage(BusMessage msg) {
						for (BusMessageConnection connection : connections) {
							if (!(msg instanceof LocalBusMessage)) {
								try {
									connection.push(msg);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});

		for (BusMessageConnection connection : connections) {
			connection.pull(internalBus.getSubscriptions()
					.newAllMessagesSubscription(), new BusMessageHandler() {
				@Override
				public void onMessage(BusMessage msg) {
					internalBus.publish(new LocalBusMessage(msg.getAttributes()));
				}
			});
			connection.start();
		}
	}

	@Override
	public void close() throws IOException {
		for (BusMessageConnection connection : connections) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
