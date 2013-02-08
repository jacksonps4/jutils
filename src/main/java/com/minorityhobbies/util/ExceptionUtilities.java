package com.minorityhobbies.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class ExceptionUtilities {
	private ExceptionUtilities() {}
	
	/**
	 * Gets the stack trace from the specified exception as a String.
	 * @param t	The exception
	 * @return	The stack trace as a string
	 */
	public static String getStackTraceAsString(Throwable t) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bos);
		t.printStackTrace(pw);
		pw.flush();
		return bos.toString();
	}
}
