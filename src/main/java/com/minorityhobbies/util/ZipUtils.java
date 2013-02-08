package com.minorityhobbies.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

public class ZipUtils {
	private ZipUtils() {
	}

	/**
	 * Reads a stream that references a ZIP archive. Reads each file and invokes
	 * the lineProcessor for every line read from each file.
	 * 
	 * @param zin
	 *            The ZIP archive stream.
	 * @param lineProcessor
	 *            The handler to be called on reading each line.
	 * @throws IOException
	 *             If an I/O error occurred reading the archive.
	 */
	public static void readLinesFromZipStream(InputStream zin,
			Closure<String, Void> lineProcessor) throws IOException {
		if (zin == null) {
			throw new RuntimeException("Stream not found");
		}
		ZipInputStream in = new ZipInputStream(zin);
		while (in.getNextEntry() != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			for (String line = null; (line = reader.readLine()) != null;) {
				if (!line.isEmpty()) {
					lineProcessor.invoke(line);
				}
			}
		}
	}
}
