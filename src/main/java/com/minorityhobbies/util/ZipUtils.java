/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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
