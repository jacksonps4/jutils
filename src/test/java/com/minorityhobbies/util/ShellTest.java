package com.minorityhobbies.util;


import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.minorityhobbies.util.Shell.ShellInterpreter;

public class ShellTest implements ShellInterpreter {
	private Shell shell;
	private PipedWriter inputWriter;
	private StringWriter out;
	private Thread shellThread;
	private StringBuilder commandReceived;
	
	@Override
	public void processCommand(String command, BufferedWriter out) throws IOException {
		commandReceived.append(command);
		out.write(command);
		out.flush();
	}

	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		commandReceived = new StringBuilder();
		inputWriter = new PipedWriter();
		shell = new Shell(new PipedReader(inputWriter), out, this);
		shellThread = new Thread(shell);
		shellThread.start();
	}
	
	@Test
	public void testSimpleCommand() throws IOException, InterruptedException {
		inputWriter.write("test command 1\n");
		for (int i = 0; commandReceived.length() == 0 && i < 20; i++) {
			Thread.sleep(100L);
		}
		assertEquals(out.toString(), shell.getPrompt() + commandReceived.toString() + shell.getPrompt());
	}
}
