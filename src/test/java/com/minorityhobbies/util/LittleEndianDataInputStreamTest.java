package com.minorityhobbies.util;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class LittleEndianDataInputStreamTest {
	private static final short NUMBER = 1000;
	private byte[] little;
	private byte[] big;
	private LittleEndianDataInputStream littleIn;
	private DataInputStream bigIn;
	
	@Before
	public void setUp() throws Exception {
		final int len = 2;
		
		big = new byte[len];
		little = new byte[len];
		
		ByteBuffer buf = ByteBuffer.allocate(len);
		buf.order(BIG_ENDIAN);
		buf.putShort(NUMBER);
		buf.flip();
		buf.get(big);
		
		buf.clear();
		buf.order(LITTLE_ENDIAN);
		buf.putShort(NUMBER);
		buf.flip();
		buf.get(little);
		
		littleIn = new LittleEndianDataInputStream(new ByteArrayInputStream(little));
		bigIn = new DataInputStream(new ByteArrayInputStream(big));
	}

	@Test
	public void testLEReadShort() throws IOException {
		assertEquals(NUMBER, littleIn.readShort());
	}
	
	@Test
	public void testBEReadShort() throws IOException {
		assertEquals(NUMBER, bigIn.readShort());
	}
}
