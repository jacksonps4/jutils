/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.minorityhobbies.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	private StreamUtils() {
	}

	public static String readLine(InputStream in) throws IOException {
		StringBuilder line = new StringBuilder();
		int count = 0;
		int v = 0;
		while (count == 0 || v != 10) {
			v = in.read();
			if (line.length() == 0) {
				if (v == -1) {
					return null;
				}
				if (v == 10) {
					return "";
				}
			}
			if (v == -1) {
				break;
			}
			if (v != 10 && v != 13) {
				char c = (char) v;
				line.append(c);
				count++;
			}
		}
		return line.toString();
	}

	public static String readAll(InputStream in) throws IOException {
		StringBuilder data = new StringBuilder();
		byte[] b = new byte[1024 * 64];
		for (int read = 0; (read = in.read(b)) > -1;) {
			data.append(new String(b, 0, read));
		}
		return data.toString();
	}
}
