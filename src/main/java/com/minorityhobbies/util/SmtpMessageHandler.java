/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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
