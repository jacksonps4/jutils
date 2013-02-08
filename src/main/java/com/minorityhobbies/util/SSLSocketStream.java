package com.minorityhobbies.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketStream {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final String hostname;
	private final int port;
	private final InputStream input;
	private final OutputStream output;
	private Socket socket;
	private Thread reader;
	private Thread writer;

	public SSLSocketStream(InputStream in, OutputStream out, String hostname,
			int port) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.input = in;
		this.output = out;
	}

	public void connect() throws IOException {
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		socket = socketFactory.createSocket(hostname, port);
		logger.info(String.format("Connected to %s:%d", hostname, port));
	}

	public void start() {
		reader = new Thread() {
			public void run() {
				logger.info(String.format("Reader started"));
				try {
					InputStream in = socket.getInputStream();
					while (!Thread.currentThread().isInterrupted()) {
						int v = in.read();
						if (v == -1) {
							break;
						}
						if (v > 0) {
							output.write(v);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		reader.start();

		writer = new Thread() {
			public void run() {
				logger.info(String.format("Writer started"));
				try {
					OutputStream out = socket.getOutputStream();
					while (!Thread.currentThread().isInterrupted()) {
						int v = input.read();
						if (v == -1) {
							break;
						}
						if (v > 0) {
							out.write(v);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		writer.start();
	}

	public static void main(String[] args) throws IOException {
		JULDebugLogConfiguration.use();
		
		SSLSocketStream stream = new SSLSocketStream(System.in, System.out,
				args[0], Integer.parseInt(args[1]));
		stream.connect();
		stream.start();
	}
}
