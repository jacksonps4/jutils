package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class PropertiesObjectRepositoryStoreObjectTest {
	private File location;
	private PropertiesObjectRepository repository;

	private static class SimplePoint implements Serializable {
		private static final long serialVersionUID = 6496913383281617368L;

		private final int x;
		private final int y;

		public SimplePoint(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	private static class Simple3DPoint extends SimplePoint {
		private static final long serialVersionUID = -6235859682458588712L;

		private final int z;

		public Simple3DPoint(int x, int y, int z) {
			super(x, y);
			this.z = z;
		}

		public int getZ() {
			return z;
		}
	}

	@Before
	public void setUp() throws IOException {
		location = File.createTempFile("testRepository", ".properties");
		repository = new PropertiesObjectRepository(location);

		SimplePoint p = new SimplePoint(100, 250);
		repository.store("p", p);

		Simple3DPoint q = new Simple3DPoint(5, 10, 15);
		repository.store("q", q);
		
		Simple3DPoint r = new Simple3DPoint(7, 14, 21);
		repository.store("r", r);

	}

	@After
	public void tearDown() throws IOException {
		assertTrue(location.delete());
	}

	@Test
	public void testSimplePointIsWrittenToFile() throws IOException,
			ClassNotFoundException {
		Properties props = readProperties();
		String raw = props.getProperty("p");
		
		BASE64Decoder dec = new BASE64Decoder();
		byte[] b = dec.decodeBuffer(raw);
		ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(b));
		SimplePoint p = (SimplePoint) oin.readObject();
		assertEquals(100, p.getX());
		assertEquals(250, p.getY());
	}

	@Test
	public void testSimple3DPointIsWrittenToFile() throws IOException,
			ClassNotFoundException {
		Properties props = readProperties();
		String raw = props.getProperty("q");
		
		BASE64Decoder dec = new BASE64Decoder();
		byte[] b = dec.decodeBuffer(raw);

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				b));
		Simple3DPoint p = (Simple3DPoint) in.readObject();
		assertEquals(5, p.getX());
		assertEquals(10, p.getY());
		assertEquals(15, p.getZ());
	}

	@Test
	public void testRemoveFromMemoryIndex() throws IOException {
		repository.remove("q");
		assertNull(repository.retrieve("q", String.class));
	}

	@Test
	public void testRemoveFromDisk() throws IOException {
		assertNotNull(repository.retrieve("q", Simple3DPoint.class));
		repository.remove("q");

		repository = new PropertiesObjectRepository(location);
		assertNull(repository.retrieve("q", Simple3DPoint.class));
	}

	private Properties readProperties() throws FileNotFoundException,
			IOException {
		Properties props = new Properties();
		DataInputStream in = null;
		try {
			in = new DataInputStream(new FileInputStream(location));
			while (!Thread.currentThread().isInterrupted()) {
				String key = in.readUTF();
				int length = in.readInt();
				if (length == -1) {
					length = in.readInt();
				}
				byte[] b = new byte[length];
				in.readFully(b);

				BASE64Encoder enc = new BASE64Encoder(); 
				props.put(key, enc.encode(b));
			}
		} catch (EOFException e) {
			// end of file
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}

}
