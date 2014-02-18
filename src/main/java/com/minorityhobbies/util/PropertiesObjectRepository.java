package com.minorityhobbies.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple repository designed for a relatively small number of objects (of the
 * order of tens-of-thousands rather than millions).
 * 
 * Rules: (1) the getter method for a property must return an object which when
 * serialised using toString() produces a representation that when passed into
 * the corresponding field directly or invoking a setter method causes the
 * getter to return the same value again.
 */
public class PropertiesObjectRepository {
	private final FileInputStream in;
	private final FileOutputStream out;
	private final FileChannel fc;
	private final Map<String, ObjectRecord> index = new HashMap<String, ObjectRecord>();

	private static final class ObjectRecord {
		final long startPosition;

		public ObjectRecord(long startPosition) {
			super();
			this.startPosition = startPosition;
		}
	}

	public PropertiesObjectRepository(File repository) throws IOException {
		super();
		this.in = new FileInputStream(repository);
		this.out = new FileOutputStream(repository);
		this.fc = in.getChannel();
		createIndex();
	}

	void createIndex() throws IOException {
		long recordStart = 0;
		DataInputStream data = new DataInputStream(in);
		try {
			recordStart = fc.position();
			String key = data.readUTF();
			int payloadLength = data.readInt();
			
			byte[] b = new byte[payloadLength];
			data.readFully(b);
			
			index.put(key, new ObjectRecord(recordStart));
		} catch (EOFException e) {
			// sorry way to detect EOF
		}
	}

	<T> T retrieve(String key, Class<T> type) throws IOException {
		ObjectRecord indexEntry = index.get(key);
		fc.position(indexEntry.startPosition);

		DataInputStream d = new DataInputStream(in);
		String keyRead = d.readUTF();
		if (!keyRead.equals(key)) {
			throw new IllegalStateException(String.format("Database corrupted: expected key = '%s' but found '%s'", key, keyRead));
		}
		
		int length = d.readInt(); 
		byte[] b = new byte[length];
		d.readFully(b);
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
		try {
			@SuppressWarnings("unchecked")
			T obj = (T) in.readObject();
			return obj;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	void store(String key, Object obj) throws IOException {
		ByteArrayOutputStream rawObject = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(rawObject);
		oos.writeObject(obj);
		oos.flush();
		byte[] rawObj = rawObject.toByteArray();
		
		ByteArrayOutputStream rawEntry = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(rawEntry);
		data.writeUTF(key);
		data.writeInt(rawObj.length);
		data.write(rawObj);
		data.flush();
		
		byte[] rawObjEntry = rawEntry.toByteArray();
		
		// move to end of file
		fc.position(fc.size());
		out.write(rawObjEntry);
		out.flush();

		long endPosition = fc.size() - 1;
		long startPosition = endPosition - rawObjEntry.length + 1;
		index.put(key, new ObjectRecord(startPosition));
	}

	void set(String key, String value) throws IOException {
		
	}
}
