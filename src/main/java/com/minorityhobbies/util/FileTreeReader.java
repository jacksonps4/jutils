package com.minorityhobbies.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.LinkedList;
import java.util.List;

public class FileTreeReader {
	private final Reader reader;

	public FileTreeReader(File dir) throws IOException {
		this(dir, null);
	}

	public FileTreeReader(File dir, FilenameFilter filter) throws IOException {
		List<InputStream> streams = new LinkedList<>();
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
				streams.add(new FileInputStream(f));
			}
		}
		this.reader = new InputStreamReader(
				new SequenceInputStream(new CollectionEnumeration<>(streams)));
	}

	public Reader getReader() {
		return reader;
	}
}
