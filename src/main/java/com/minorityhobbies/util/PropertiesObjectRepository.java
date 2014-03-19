/*
Copyright (c) 2014 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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
	private final FileChannel fcIn;
	private final FileChannel fcOut;
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
		this.fcIn = in.getChannel();
		this.fcOut = out.getChannel();
		createIndex();
	}

	void createIndex() throws IOException {
		long recordStart = 0;
		DataInputStream data = new DataInputStream(in);
		try {
			recordStart = fcIn.position();
			String key = data.readUTF();
			int payloadLength = data.readInt();
			if (payloadLength == -1) {
				payloadLength = data.readInt();
			}
			
			byte[] b = new byte[payloadLength];
			data.readFully(b);
			
			index.put(key, new ObjectRecord(recordStart));
		} catch (EOFException e) {
			// sorry way to detect EOF
		}
	}

	public <T> T retrieve(String key, Class<T> type) throws IOException {
		ObjectRecord indexEntry = index.get(key);
		if (indexEntry == null) {
			return null;
		}
		
		fcIn.position(indexEntry.startPosition);

		readKey(key);
		DataInputStream d = new DataInputStream(in);
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

	private String readKey(String key) throws IOException {
		DataInputStream d = new DataInputStream(in);
		String keyRead = d.readUTF();
		if (!keyRead.equals(key)) {
			throw new IllegalStateException(String.format("Database corrupted: expected key = '%s' but found '%s'", key, keyRead));
		}
		return keyRead;
	}

	public void store(String key, Object obj) throws IOException {
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
		fcOut.position(fcOut.size());
		out.write(rawObjEntry);
		out.flush();

		long endPosition = fcOut.size() - 1;
		long startPosition = endPosition - rawObjEntry.length + 1;
		index.put(key, new ObjectRecord(startPosition));
	}

	public boolean remove(String key) throws IOException {
		ObjectRecord indexEntry = index.get(key);
		if (indexEntry != null) {
			fcIn.position(indexEntry.startPosition);
			
			readKey(key);
			DataInputStream d = new DataInputStream(in);
			int size = d.readInt();
			DataOutputStream o = new DataOutputStream(out);
			fcOut.position(fcIn.position() - 4);
			
			o.writeInt(-1);
			o.writeInt(size - 4);
			o.flush();
			
			index.remove(key);
			
			return true;
		} else {
			return false;
		}
	}
}
