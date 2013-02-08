package com.minorityhobbies.util;

import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JULDebugLogConfiguration {
    private final LogManager logManager;
    private final Logger rootLogger;
    private final Handler defaultHandler = new JULStandardOutputConsoleHandler();
    private final Formatter defaultFormatter = new JULDefaultLogFormatter();

    public JULDebugLogConfiguration() throws SecurityException, IOException {
        super();

        this.logManager = LogManager.getLogManager();
        this.rootLogger = logManager.getLogger("");
        configure();
    }
    
	public static void use() throws SecurityException, IOException {
		System.clearProperty("java.util.logging.config.file");
		System.setProperty("java.util.logging.config.class",
				JULDebugLogConfiguration.class.getName());
        
		LogManager logManager = LogManager.getLogManager();
        logManager.reset();
        logManager.readConfiguration();
	}

    final void configure() {
        defaultHandler.setFormatter(defaultFormatter);
        defaultHandler.setLevel(Level.FINE);
        rootLogger.setLevel(Level.FINE);
        rootLogger.addHandler(defaultHandler);
        logManager.addLogger(rootLogger);
    }   
}