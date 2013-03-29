/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class Shell implements Runnable {
	public interface ShellInterpreter {
		void processCommand(String command, BufferedWriter out) throws IOException;
	}
	
	static final String DEFAULT_PROMPT = "> ";
	private final BufferedReader in;
	private final BufferedWriter out;
	private final ShellInterpreter interpreter;
	private String prompt = DEFAULT_PROMPT;
	
	public Shell(InputStream in, OutputStream out, ShellInterpreter interpreter) {
		super();
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new OutputStreamWriter(out));
		this.interpreter = interpreter;
	}

	public Shell(Reader in, Writer out,
			ShellInterpreter interpreter) {
		super();
		this.in = new BufferedReader(in);
		this.out = new BufferedWriter(out);
		this.interpreter = interpreter;
	}

	@Override
	public void run() {
		try {
			for (String line = null; !Thread.currentThread().isInterrupted(); ) {
				prompt();
				if (line != null && line.endsWith("\\")) {
					line = line.concat(in.readLine());
				} else {
					line = in.readLine();
				}
				if (line == null) {
					break;
				}
				try {
					interpreter.processCommand(line, out);
				} catch (Exception e) {
					out.write(String.format("Failed to process command '%s': %s%n", line, e.getMessage()));
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	private void prompt() throws IOException {
		out.write(prompt);
		out.flush();
	}
}
