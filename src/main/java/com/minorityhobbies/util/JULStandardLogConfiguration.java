/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Provides a simple way to use the built-in Java {@link Logger} framework. 
 * By default, the configuration is a little tricky to set up. This gives
 * a straightforward method of making your application format its logs in
 * a sensible fashion using Java logging.
 * 
 * Using this class sets the log level to INFO. 
 * For FINE, use {@link JULDebugLogConfiguration}.
 */
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
