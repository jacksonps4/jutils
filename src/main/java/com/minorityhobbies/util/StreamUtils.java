package com.minorityhobbies.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	private StreamUtils() {}
	
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
}
