package com.minorityhobbies.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Ignore;

@Ignore
class ReliableSocketConnectionTest {
	public static void main(String[] args) throws IOException {
		final String hostname = args[0];
		final int port = Integer.parseInt(args[1]);

		boolean useSSL = false;
		if (System.getProperty("useSSL") != null) {
			useSSL = true;
		}

		ReliableSocketConnection conn = new ReliableSocketConnection(hostname, port, new ReliableConnectionHandler() {
			@Override
			public void processData(byte[] b, int offset, int length) {
				System.out.print(new String(b, offset, length));
			}
			
			@Override
			public void onDisconnected() {
				System.out.println("Disconnected from " + hostname + ":" + port);
			}
			
			@Override
			public void onConnected() {
				System.out.println("Connected to " + hostname + ":" + port);
			}
		}, useSSL);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		for (String line = null; (line = reader.readLine()) != null; ) {
			byte[] b = line.concat("\n").getBytes();
			conn.write(b, 0, b.length);
			conn.flush();
		}
		
		if (conn != null) {
			conn.close();
		}
	}
}
