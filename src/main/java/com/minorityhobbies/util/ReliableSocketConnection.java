package com.minorityhobbies.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

/**
 * A reliable socket connection which handles disconnections with
 * automated backoff and reconnection. 
 */
public class ReliableSocketConnection implements Flushable, Closeable {
	private static final int MAX_BACKOFF_SECONDS = 300;
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final ExecutorService executor;
	private final String hostname;
	private final int port;
	private final ReliableConnectionHandler connectionHandler;
	private Socket socket;
	private int backOffSeconds;
	private final boolean useSSL;

	public ReliableSocketConnection(String hostname, int port,
			ReliableConnectionHandler connectionHandler, boolean useSSL) {
		super();
		this.hostname = hostname;
		this.port = port;
		if (connectionHandler == null) {
			throw new IllegalArgumentException(
					"Must specify a connection handler");
		}
		this.useSSL = useSSL;
		this.connectionHandler = connectionHandler;
		executor = Executors.newSingleThreadExecutor();
		
		connect();
	}

	public ReliableSocketConnection(String hostname, int port,
			ReliableConnectionHandler connectionHandler) {
		this(hostname, port, connectionHandler, false);
	}
	
	void connect() {
		executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					dispatchReceivedData();
				} catch (Throwable t) {
					StringWriter writer = new StringWriter();
					t.printStackTrace(new PrintWriter(writer));
					logger.severe("Reliable socket connection failed\n"
							+ writer.toString());
				}
				return null;
			}
		});
	}

	void dispatchReceivedData() {
		final byte[] b = new byte[1024 * 64];
		useSocketReliably(new SocketAction() {
			@Override
			public void doAction(InputStream in, OutputStream out)
					throws IOException {
				for (int read = 0; (read = in.read(b)) > -1;) {
					if (read > 0) {
						connectionHandler.processData(b, 0, read);
					} else {
						Thread.yield();
					}
				}
				throw new EOFException();
			}
		});
	}

	private interface SocketAction {
		void doAction(InputStream in, OutputStream out) throws IOException;
	}

	void useSocketReliably(SocketAction action) {
		boolean actionDone = false;
		while (!actionDone) {
			try {
				establishConnection();
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				action.doAction(in, out);
				actionDone = true;
			} catch (IOException e) {
				backOff();
				disconnected();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void establishConnection() throws IOException, InterruptedException {
		Thread.sleep(TimeUnit.MILLISECONDS.convert(backOffSeconds,
				TimeUnit.SECONDS));

		synchronized (this) {
			if (socket == null) {
				if (useSSL) {
					socket = SSLSocketFactory.getDefault().createSocket(
							hostname, port);
				} else {
					socket = new Socket(hostname, port);
				}

				connectionHandler.onConnected();

				logger.info(String.format("Connection established to %s:%d",
						hostname, port));
			}
		}
	}

	private void backOff() {
		if (backOffSeconds == 0) {
			backOffSeconds = 1;
		} else if (backOffSeconds < MAX_BACKOFF_SECONDS) {
			backOffSeconds *= 2;
		} else {
			backOffSeconds = MAX_BACKOFF_SECONDS;
		}

		logger.warning(String
				.format("Disconnected from %s:%d - backing off: next try in %d seconds",
						hostname, port, backOffSeconds));
	}
	
	private void disconnected() {
		synchronized (this) {
			if (socket != null) {
				socket = null;
				connectionHandler.onDisconnected();
			}
		}
	}

	public void write(final byte[] b, final int offset, final int length) {
		useSocketReliably(new SocketAction() {
			@Override
			public void doAction(InputStream in, OutputStream out) throws IOException {
				synchronized (this) {
					out.write(b, offset, length);
				}				
			}
		});
	}

	public void flush() {
		useSocketReliably(new SocketAction() {
			@Override
			public void doAction(InputStream in, OutputStream out) throws IOException {
				synchronized (this) {
					out.flush();
				}				
			}
		});
	}

	@Override
	public void close() throws IOException {
		if (executor != null) {
			executor.shutdownNow();
		}

		synchronized (this) {
			if (socket != null) {
				try {
					socket.close();
					connectionHandler.onDisconnected();
				} catch (IOException e) {
					logger.severe("Failed to close socket");
				}
			}
		}
	}
}
