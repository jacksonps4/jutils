package com.minorityhobbies.util.bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

class StandardBusMessageSerialiser {
	public byte[] toBytes(BusMessage msg) throws IOException {
		StringBuilder serialForm = new StringBuilder();
		for (Map.Entry<String, String> entry : msg.getAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			serialForm.append(String.format("%s%s%s%n", key, '\u0003', value));
		}
		serialForm.append('\u0004');
		return serialForm.toString().getBytes(Charset.forName("UTF-8"));
	}
	
	public BusMessage fromBytes(byte[] msg) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new StringReader(new String(msg, Charset.forName("UTF-8"))));
			Map<String, String> msgValues = new HashMap<String, String>();
			for (String line = null; (line = reader.readLine()) != null; ) {
				String[] attributes = line.split("\u0003");
				if (attributes.length != 2) {
					throw new IOException("Desserialisation failure");
				}
				String key = attributes[0];
				String value = attributes[1];
				msgValues.put(key, value);
			}
			return new StandardBusMessage(msgValues);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
