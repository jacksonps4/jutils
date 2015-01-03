/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.LinkedList;
import java.util.List;

public class StringUtils {
	private StringUtils() {}
	
	public static String generateRandomPayload(int length) {
		StringBuilder random = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int chr = (int) (Math.random() * 26) + 65;
			boolean uppercase = Math.random() >= 0.5 ? true : false;
			if (uppercase) {
				chr = chr + 32;
			}
			random.append((char) chr);
		}
		return random.toString();
	}
	
	public static String[] splitQuoted(String data, char delimiter) {
		boolean inDoubleQuotes = false;
		
		List<String> fields = new LinkedList<String>();
		StringBuilder field = new StringBuilder();
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '"') {
				inDoubleQuotes = !inDoubleQuotes;
			} else if (c == delimiter) {
				if (!inDoubleQuotes) {
					fields.add(field.toString());
					field = new StringBuilder();
				} else {
					field.append(c);
				}
			} else {
				field.append(c);
			}
		}
		if (!inDoubleQuotes) {
			fields.add(field.toString());
		} 
		
		return fields.toArray(new String[fields.size()]);
	}
	
	public static String convertSnakeCaseToCamelCase(String value) {
	    StringBuilder result = new StringBuilder();
		char[] v = value.toCharArray();
	    for (int i = 0; i < v.length; i++) {
	    	char c = v[i];
	    	if (c == '_') {
	    		if ((i + 1) < v.length) {
	    			result.append(Character.toUpperCase(v[++i]));
	    		}
	    	} else {
	    		result.append(c);
	    	}
	    }
	    return result.toString();
	}
	
	public static String anyToCamelCase(String any) {
		StringBuilder result = new StringBuilder();
		char[] cb = any.toCharArray();
		boolean nextUpper = false;
		for (int i = 0; i < cb.length; i++) {
			char c = cb[i];

			if (i == 0) {
				result.append(Character.toLowerCase(c));
				continue;
			}

			if (Character.isWhitespace(c)
					|| !(Character.isAlphabetic(c) || Character.isDigit(c))) {
				nextUpper = true;
				continue;
			}

			if (nextUpper) {
				nextUpper = false;
				result.append(Character.toUpperCase(c));
				continue;
			}

			result.append(c);
		}
		return result.toString();
	}
}
