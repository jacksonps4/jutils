package com.minorityhobbies.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.FileChannel;

public class FileTailReader extends Reader {
	private final File file;
	private FileInputStream in;
	private FileChannel fc;
	private int read;
	
	public FileTailReader(File file) throws IOException {
		this.file = file;
		openFile();
	}

	private void openFile() throws IOException {
		in = new FileInputStream(file);
		fc = in.getChannel();
		fc.position(read);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (!fc.isOpen()) {
			openFile();
		}

		long size = fc.size();
		long pos = fc.position();
		long byteInFileNotYetRead = size - pos;
		int readThisTime = readData(cbuf, off, len, (int) byteInFileNotYetRead);
		read += readThisTime;
		return readThisTime;
	}

	int readData(char[] cbuf, int off, int len, int remaining) throws IOException {
		// available data: return immediately
		int availableBufferSpace = len - off;
		if (availableBufferSpace < remaining) {
			// fill buffer space with availableBufferSpace bytes from file
			for (int i = off; i < cbuf.length; i++) {
				cbuf[i] = (char) in.read();
			}
			return availableBufferSpace;
		} else {
			// fill buffer with remaining bytes from file
			for (int i = off; i < (off + remaining); i++) {
				cbuf[i] = (char) in.read();
			}
			
			in.close();
			fc.close();
			
			return remaining;
		}
	}

	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
	}
}
