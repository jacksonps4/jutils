package com.minorityhobbies.util;

import java.util.Map;

/**
 * Handler for dealing with messages from {@link SmtpServer}. The handler is a
 * state machine which operates as follows. Firstly, newMessage() will be called
 * with any headers that are provided in the message. Then, messageData will be
 * called with some message data until either there is no more data, in which
 * case endOfMessage() will be called or until an error occurs, when error()
 * will be called.
 */
public interface SmtpMessageHandler {
	/**
	 * Indicates that new message processing should begin.
	 * 
	 * @param headers
	 *            Message headers.
	 * @return A unique identifier for this message.
	 * @throws SmtpException
	 *             If the message cannot be processed
	 */
	long newMessage(Map<String, String> headers) throws SmtpException;

	/**
	 * Called when message data is received.
	 * 
	 * @param data
	 *            Some data. A part of the message.
	 */
	void messageData(String data);

	/**
	 * Called if the current message was not completely received. If this method
	 * is called, the current message processing should stop.
	 * 
	 * @param e
	 *            The details of the failure,
	 */
	void error(Exception e);

	/**
	 * Indicates the end of the current message and that it was successfully
	 * received.
	 * 
	 * @return True if the message was accepted by the handler. False otherwise.
	 */
	boolean endOfMessage();
}
