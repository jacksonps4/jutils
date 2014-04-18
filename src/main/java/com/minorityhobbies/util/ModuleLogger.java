/*
Copyright (c) 2014 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.logging.Logger;

public class ModuleLogger {
	private final Logger logger;

	public ModuleLogger(String name) {
		super();
		logger = Logger.getLogger(name);
	}

	public void trace(String msg, Object... params) {
		logger.finer(String.format(msg, params));
	}

	public void debug(String msg, Object... params) {
		logger.fine(String.format(msg, params));
	}

	public void info(String msg, Object... params) {
		logger.info(String.format(msg, params));
	}

	public void warn(String msg, Object... params) {
		logger.warning(String.format(msg, params));
	}

	public void error(String msg, Throwable t, Object... params) {
		logger.throwing(String.format(msg, params), "", t);
	}
}
