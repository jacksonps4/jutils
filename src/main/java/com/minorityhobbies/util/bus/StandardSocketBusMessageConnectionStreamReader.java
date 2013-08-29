package com.minorityhobbies.util.bus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

final class StandardSocketBusMessageConnectionStreamReader implements Callable<Void> {
	private final InputStream in;
	private final StandardBusMessageSerialiser serialiser;
	private final BusMessageHandler handler;
	
	public StandardSocketBusMessageConnectionStreamReader(InputStream in,
			StandardBusMessageSerialiser serialiser, BusMessageHandler handler) {
		super();
		this.in = in;
		this.serialiser = serialiser;
		this.handler = handler;
	}

	@Override
	public Void call() throws Exception {
		while (!Thread.currentThread().isInterrupted()) {
			read();
		}
		return null;
	}

	void read() throws IOException {
		byte[] b = new byte[1024 * 32];
		for (int read = 0; (read = in.read(b)) > -1; ) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 0; i < read; i++) {
				if (b[i] != 4) {
					bos.write(b[i]);
				} else {
					handler.onMessage(serialiser.fromBytes(Arrays.copyOfRange(b, 0, i)));
					bos = new ByteArrayOutputStream();
				}
			}
		}
	}
}