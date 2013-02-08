package com.minorityhobbies.util;

class SmtpQuitException extends Exception {
	private static final long serialVersionUID = 8696008572213455117L;

	public SmtpQuitException() {
		super();
	}

	public SmtpQuitException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SmtpQuitException(String arg0) {
		super(arg0);
	}

	public SmtpQuitException(Throwable arg0) {
		super(arg0);
	}
}
