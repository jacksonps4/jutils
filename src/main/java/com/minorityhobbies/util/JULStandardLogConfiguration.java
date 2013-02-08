package com.minorityhobbies.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JULStandardLogConfiguration {
	private final LogManager logManager;
	private final Logger rootLogger;
	private final ConsoleHandler defaultHandler = new JULStandardOutputConsoleHandler();
	private final Formatter defaultFormatter = new JULDefaultLogFormatter();
	
	public JULStandardLogConfiguration() {
		super();

		this.logManager = LogManager.getLogManager();
		this.rootLogger = Logger.getLogger("");
		
		configure();
	}

	public static void use() throws SecurityException, IOException {
		System.setProperty("java.util.logging.config.class",
				JULStandardLogConfiguration.class.getName());
		LogManager.getLogManager().reset();
		LogManager.getLogManager().readConfiguration();
	}

	final void configure() {
		defaultHandler.setFormatter(defaultFormatter);
		defaultHandler.setLevel(Level.INFO);
		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(defaultHandler);
		logManager.addLogger(rootLogger);
	}
}
