package com.minorityhobbies.util;

public class SmtpException extends Exception {
	private static final long serialVersionUID = -8844949163689240824L;
	private final int errorCode;
	
	public SmtpException(String arg0, int errorCode) {
		super(arg0);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
