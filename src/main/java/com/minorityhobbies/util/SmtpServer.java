package com.minorityhobbies.util;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very basic SMTP server. This allows for messages to be transferred easily
 * when SMTP is available as a transport.
 * 
 * The easiest way to create a server is to provide a port number on which to
 * listen and an {@link SmtpMessageHandler} to process the messages. This
 * returns a {@link Closeable} which allows the server to be closed down later.
 * 
 */
public class SmtpServer implements Runnable {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final Pattern heloPattern = Pattern.compile("(EHLO)|(HELO) (.*)", CASE_INSENSITIVE);
	private final Pattern headerPattern = Pattern.compile("(.*)?:(.*)");

	private final SmtpMessageHandler mailbox;
	private final Socket connection;

	public SmtpServer(Socket connection, SmtpMessageHandler mailbox) {
		super();
		this.connection = connection;
		this.mailbox = mailbox;
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					connection.getOutputStream()));

			sendResponse(220, "Postman Pat: READY", writer);
			String hostname = null;
			boolean greeted = false;
			while (!greeted) {
				String greeting = reader.readLine();
				checkForQuit(greeting);
				if ((hostname = parseHelo(greeting, reader)) == null) {
					sendResponse(501, String.format(
							"Unrecognised HELO / ELHO: %s", greeting), writer);
				} else {
					greeted = true;
				}
			}
			sendResponse(250,
					String.format("HELO %s - Welcome to Greendale", hostname),
					writer);

			while (connection.isConnected()) {
				logger.fine(String.format("New connection from: %s", connection.getRemoteSocketAddress()));
				try {
					for (boolean processed = false; !processed;) {
						Map<String, String> headers = new CaseInsensitiveMap<String>(new HashMap<String, String>());
						boolean gotHeaders = false;
						logger.fine("Reading headers");
						while (!gotHeaders) {
							String command = null;
							while (!Thread.currentThread().isInterrupted()) {
								command = reader.readLine();
								logger.fine(String.format("Header: %s", command));
								if (command == null) {
									throw new SmtpQuitException(
											String.format(
													"Lost connection to %s",
													connection));
								}
								if (command.equals("DATA")) {
									break;
								}

								checkForCommonCommands(command);
								readHeader(command, headers);
								sendResponse(250, String.format("OK"), writer);
							}

							if (!checkForValidHeaders(headers)) {
								sendResponse(
										503,
										String.format("Missing required headers"),
										writer);
								logger.warning("Missing RCPT TO or MAIL FROM");
							} else {
								gotHeaders = true;
							}
						}

						long messageId = -1;
						try {
							messageId = mailbox.newMessage(headers);
						} catch (SmtpException e) {
							sendResponse(
									e.getErrorCode(),
									e.getMessage(),
									writer);
							continue;
						}
						
						sendResponse(354,
								"Send message body - end with <CR>.<CR>",
								writer);
						logger.fine("Receiving message body");
						
						String data = null;
						int receivedBytes = 0;
						while (!Thread.currentThread().isInterrupted()) {
							data = reader.readLine();
							if (data == null) {
								SmtpQuitException e = new SmtpQuitException(
										String.format("Lost connection to %s",
												connection));
								mailbox.error(e);
								throw e;
							}
							if (data.equals(".")) {
								break;
							}
							mailbox.messageData(String.format("%s\n", data));
							receivedBytes += data.getBytes().length;
						}

						logger.fine(String.format("Finished processing message: %d bytes received", receivedBytes));
						
						if (mailbox.endOfMessage()) {
							processed = true;
							sendResponse(250, "Message queued for delivery",
									writer);
							logger.info(String.format(
									"Successfully processed message id = %d",
									messageId));
						} else {
							logger.severe(String.format(
									"Failed to process message with id = %d",
									messageId));
						}
					}
				} catch (SocketException e) {
					break;
				} catch (SmtpResetException e) {
					sendResponse(250, "OK", writer);
					continue;
				}
			}
		} catch (SmtpQuitException e) {
			try {
				connection.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean checkForValidHeaders(Map<String, String> headers) {
		return headers.containsKey("MAIL FROM")
				&& headers.containsKey("RCPT TO");
	}

	private void checkForCommonCommands(String command)
			throws SmtpResetException, SmtpQuitException {
		checkForReset(command);
		checkForQuit(command);
	}

	private void checkForReset(String command) throws SmtpResetException {
		if (command.trim().equalsIgnoreCase("RSET")) {
			throw new SmtpResetException();
		}
	}

	private void checkForQuit(String command) throws SmtpQuitException {
		if (command.trim().equalsIgnoreCase("QUIT")) {
			throw new SmtpQuitException();
		}
	}

	private String parseHelo(String line, BufferedReader reader)
			throws IOException {
		Matcher matcher = heloPattern.matcher(line);
		if (matcher.find()) {
			return matcher.replaceAll("$1");
		}

		return null;
	}

	private void readHeader(String command, Map<String, String> headers)
			throws IOException {
		Matcher matcher = headerPattern.matcher(command);
		String k = matcher.replaceAll("$1");
		String v = matcher.replaceAll("$2");
		headers.put(k, v);
	}

	private void sendResponse(int responseCode, String details,
			BufferedWriter writer) throws IOException {
		writer.write(String.format("%d %s\n", responseCode, details));
		writer.flush();
	}

	private static class ConsoleSmtpMessageHandler implements
			SmtpMessageHandler {
		private StringBuilder message;
		private final AtomicLong currentMessageId = new AtomicLong();

		@Override
		public long newMessage(Map<String, String> headers) {
			message = new StringBuilder();
			return currentMessageId.incrementAndGet();
		}

		@Override
		public void messageData(String data) {
			message.append(data);
			message.append("\n");
		}

		@Override
		public void error(Exception e) {
			System.err.println("Failed to process message");
			e.printStackTrace();
		}

		@Override
		public boolean endOfMessage() {
			System.out.printf("New message: id = %d\n", currentMessageId.get());
			System.out.println(message.toString());
			System.out.printf("\n\n\n");
			return true;
		}
	}

	/**
	 * Creates a new SMTP server.
	 * 
	 * @param port
	 *            The port to which to bind this server.
	 * @param handler
	 *            The message handler for the server.
	 * @return A handle to stop the server.
	 */
	public static Closeable createSmtpServer(final int port,
			final SmtpMessageHandler handler) {
		final Thread server = new Thread(String.format("SMTP server: port %d",
				port)) {
			public void run() {
				ServerSocket serverSocket = null;
				try {
					serverSocket = new ServerSocket(port);
					while (!Thread.currentThread().isInterrupted()) {
						Socket connection = null;
						try {
							connection = serverSocket.accept();
							SmtpServer server = new SmtpServer(connection,
									handler);
							server.run();
						} finally {
							connection.close();
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					if (serverSocket != null) {
						try {
							serverSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		};
		server.start();
		return new Closeable() {
			@Override
			public void close() throws IOException {
				server.interrupt();
			}
		};
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			throw new IllegalArgumentException(
					"Please specify a port as the first argument");
		}

		final int port = Integer.parseInt(args[0]);
		System.out.println("Creating SMTP server on port " + port);
		SmtpMessageHandler handler = new ConsoleSmtpMessageHandler();
		final Closeable handle = createSmtpServer(port,
				handler);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					System.out.println("Stopping SMTP server on port " + port);
					handle.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}