package com.minorityhobbies.util;

import java.util.logging.ConsoleHandler;

final class JULStandardOutputConsoleHandler extends ConsoleHandler {
	public JULStandardOutputConsoleHandler() {
		super();
		setOutputStream(System.out);
	}
}