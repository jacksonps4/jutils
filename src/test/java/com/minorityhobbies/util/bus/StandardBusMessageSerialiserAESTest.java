package com.minorityhobbies.util.bus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class StandardBusMessageSerialiserAESTest {
	@Test
	public void testEncryptAndDecrypt() throws IOException {
		BusMessageSerialiser serialiser = StandardBusMessageSerialisers.AES.get();
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("key1", "value1");
		BusMessage msg = new StandardBusMessage(attributes);
		byte[] b = serialiser.toBytes(msg); 
		assertNotNull(b);
		
		BusMessage receivedMsg = serialiser.fromBytes(b);
		String v1 = receivedMsg.get("key1");
		assertEquals("value1", v1);
	}
}
