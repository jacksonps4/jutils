package com.minorityhobbies.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {
	private final DataInputStream in;

	public LittleEndianDataInputStream(InputStream in) {
		super(in);
		this.in = new DataInputStream(in);
	}

	public void close() throws IOException {
		in.close();
	}

	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	public boolean markSupported() {
		return in.markSupported();
	}

	public int read() throws IOException {
		return in.read();
	}

	public final int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public final int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public final boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	public final byte readByte() throws IOException {
		return in.readByte();
	}

	public final char readChar() throws IOException {
		return Character.reverseBytes(in.readChar());
	}

	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(Long.reverseBytes(Double
				.doubleToLongBits(in.readDouble())));
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(Integer.reverseBytes(Float
				.floatToIntBits(in.readFloat())));
	}

	public final void readFully(byte[] b, int off, int len) throws IOException {
		in.readFully(b, off, len);
	}

	public final void readFully(byte[] b) throws IOException {
		in.readFully(b);
	}

	public final int readInt() throws IOException {
		return Integer.reverseBytes(in.readInt());
	}

	@SuppressWarnings("deprecation")
	public final String readLine() throws IOException {
		return in.readLine();
	}

	public final long readLong() throws IOException {
		return Long.reverseBytes(in.readLong());
	}

	public final short readShort() throws IOException {
		return Short.reverseBytes(in.readShort());
	}

	public final String readUTF() throws IOException {
		return in.readUTF();
	}

	public final int readUnsignedByte() throws IOException {
		return in.readUnsignedByte();
	}

	public final int readUnsignedShort() throws IOException {
		return in.readUnsignedShort();
	}

	public void reset() throws IOException {
		in.reset();
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public final int skipBytes(int n) throws IOException {
		return in.skipBytes(n);
	}

	public String toString() {
		return in.toString();
	}
}
