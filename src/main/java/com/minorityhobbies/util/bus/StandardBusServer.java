package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class StandardBusServer implements BusServer {
	private final Bus internalBus;
	private final BusMessageSubscriptions subscriptions;
	private final Map<BusMessageConnection, BusMessageSubscriptionHandle> connections;
	private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
	private BusMessageSubscriptionHandle handle;
	private volatile boolean started = false;

	public StandardBusServer(Bus internalBus) {
		connections = new HashMap<BusMessageConnection, BusMessageSubscriptionHandle>();

		this.internalBus = internalBus;

		subscriptions = internalBus.getSubscriptions();
	}

	@Override
	public void addConnection(BusMessageConnection connection)
			throws IOException {
		try {
			connectionLock.readLock().lock();
			BusMessageSubscriptionHandle handle = connections.get(connection);
			if (handle != null) {
				return;
			}
		} finally {
			connectionLock.readLock().unlock();
		}
		
		try {
			connectionLock.writeLock().lock();
			if (started) {
				startConnection(connection);
			} else {
				connections.put(connection, null);
			}
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private void startConnection(BusMessageConnection connection)
			throws IOException {
		// you must already have the appropriate write lock to call this method:
		BusMessageSubscriptionHandle handle = connection.pull(internalBus
				.getSubscriptions().newAllMessagesSubscription(),
				new BusMessageHandler() {
					@Override
					public void onMessage(BusMessage msg) {
						internalBus.publish(new LocalBusMessage(msg
								.getAttributes()));
					}
				});
		connections.put(connection, handle);
		connection.start();
	}

	@Override
	public void removeConnection(BusMessageConnection connection)
			throws IOException {
		try {
			connectionLock.writeLock().lock();
			BusMessageSubscriptionHandle handle = connections.remove(connection);
			handle.close();
		} catch (IOException e) { 
			e.printStackTrace();
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	@Override
	public void start() throws IOException {
		started = true;

		// push all messages received from the internal message bus to
		// all connections
		handle = internalBus.subscribe(subscriptions.newAllMessagesSubscription(),
				new BusMessageHandler() {
					@Override
					public void onMessage(BusMessage msg) {
						try {
							connectionLock.readLock().lock();
							for (BusMessageConnection connection : connections.keySet()) {
								if (!(msg instanceof LocalBusMessage)) {
									try {
										connection.push(msg);
									} catch (IOException e) {
										try {
											removeConnection(connection);
										} catch (IOException io) {
											io.printStackTrace();
										}
									}
								}
							}
						} finally {
							connectionLock.readLock().unlock();
						}
					}
				});

		// publish all messages pulled from each connection to the internal bus
		// as a LocalBusMessage
		try {
			connectionLock.writeLock().lock();
			for (BusMessageConnection connection : connections.keySet()) {
				startConnection(connection);
			}
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	@Override
	public void close() throws IOException {
		if (handle != null) {
			try {
				handle.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			connectionLock.writeLock().lock();
			for (Map.Entry<BusMessageConnection, BusMessageSubscriptionHandle> connectionEntry : connections.entrySet()) {
				BusMessageConnection connection = connectionEntry.getKey();
				BusMessageSubscriptionHandle handle = connectionEntry.getValue();
				try {
					handle.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			connectionLock.writeLock().unlock();
		}
	}
}
