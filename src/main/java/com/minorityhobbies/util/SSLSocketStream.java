/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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
