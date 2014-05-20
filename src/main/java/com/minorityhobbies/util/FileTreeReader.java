package com.minorityhobbies.util;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class FileTreeReader extends Reader {
	private final List<String> files = new LinkedList<>();
	private FileReader currentReader;
	private int currentIndex = 0;
	
	public FileTreeReader(File dir) throws IOException {
		this(dir, null);
	}

	public FileTreeReader(File dir, FilenameFilter filter) throws IOException {
		if (filter == null) {
			filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return true;
				}
			};
		}
		for (File f : FileUtils.listFilesRecursively(dir)) {
			if (filter.accept(f.getParentFile(), f.getName())) {
				files.add(f.getAbsolutePath());
			}
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (currentReader == null) {
			if (!nextReader()) {
				return -1;
			}
		}

		int read = currentReader.read(cbuf, off, len);
		while (read == -1) {
			if (!nextReader()) {
				return -1;
			}
			read = currentReader.read(cbuf, off, len);
		}
		
		return read;
	}

	private boolean nextReader() throws IOException {
		if (currentReader != null) {
			currentReader.close();
		}
		if (currentIndex < files.size()) {
			currentReader = new FileReader(files.get(currentIndex++));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void close() throws IOException {
		currentReader.close();
	}
}
