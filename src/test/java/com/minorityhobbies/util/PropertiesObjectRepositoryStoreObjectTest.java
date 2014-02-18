package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
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
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				raw.getBytes()));
		SimplePoint p = (SimplePoint) in.readObject();
		assertEquals(100, p.getX());
		assertEquals(250, p.getY());
	}

	@Test
	public void testSimple3DPointIsWrittenToFile() throws IOException,
			ClassNotFoundException {
		Properties props = readProperties();
		String raw = props.getProperty("q");
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				raw.getBytes()));
		Simple3DPoint p = (Simple3DPoint) in.readObject();
		assertEquals(5, p.getX());
		assertEquals(10, p.getY());
		assertEquals(15, p.getZ());
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
				byte[] b = new byte[length];
				in.readFully(b);

				props.put(key, new String(b));
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
