/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.minorityhobbies.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utilities for reading files easily.
 * 
 */
public class FileUtils {
	private FileUtils() {
	}

	/**
	 * Reads the specified file and returns its contents.
	 * 
	 * @param file
	 *            The file to be read.
	 * @return The contents of the file.
	 * @throws IOException
	 *             If an I/O error occurred reading the file.
	 */
	public static String readFile(File file) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] b = new byte[1024 * 64];
			StringBuilder fileData = new StringBuilder();
			for (int read = 0; (read = in.read(b)) > -1;) {
				fileData.append(new String(b, 0, read));
			}
			return fileData.toString();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Reads the specified file and returns its contents.
	 * 
	 * @param path
	 *            The absolute path to the file to be read.
	 * @return The contents of the file.
	 * @throws IOException
	 *             If an I/O error occurred reading the file.
	 */
	public static String readFile(String path) throws IOException {
		return readFile(new File(path));
	}

	/**
	 * Gets the non-directory files in the specified directory and all
	 * subdirectories.
	 * 
	 * @param dir
	 *            The directory in which to find files.
	 * @return The regular files in the specified directory and all of its
	 *         subdirectories.
	 */
	public static List<File> listFilesRecursively(File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("Cannot read null file");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Must specify a directory");
		}

		List<File> files = new LinkedList<>();
		listDirFilesRecursively(dir, files);
		return files;
	}

	private static void listDirFilesRecursively(File dir, List<File> allFiles) {
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				listDirFilesRecursively(f, allFiles);
			}
		} else {
			allFiles.add(dir);
		}
	}

	/**
	 * Gets the sub-directories in the specified directory and all
	 * sub-directories.
	 * 
	 * @param dir
	 *            The directory in which to find sub-directories.
	 * @return The sub-directories in the specified directory and all of its
	 *         sub-directories sorted in order of depth, deepest first (this
	 *         is useful for deleting recursively).
	 */
	public static List<File> listDirectoriesRecursively(File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("Cannot read null file");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Must specify a directory");
		}

		List<File> directories = listDirectoryFilesRecursively(dir);
		Collections.sort(directories, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				int f1Depth = f1.getAbsolutePath().split(System.getProperty("file.separator")).length;
				int f2Depth = f2.getAbsolutePath().split(System.getProperty("file.separator")).length;
				return (f2Depth < f1Depth) ? -1 : ((f2Depth == f1Depth) ? 0 : 1);
			}
		});
		return directories;
	}

	private static List<File> listDirectoryFilesRecursively(File dir) {
		List<File> allDirs = new LinkedList<File>();
		File[] dirFiles = dir.listFiles();
		allDirs.add(dir);
		for (File dirFile : dirFiles) {
			if (dirFile.isDirectory()) {
				allDirs.addAll(listDirectoryFilesRecursively(dirFile));
			}
		}
		return allDirs;
	}

	/**
	 * Follows the moving tail end of a file (much like UNIX 'tail -f').
	 * 
	 * @param f
	 *            The file
	 * @param newLineCallback
	 *            The callback which will be invoked upon each new line.
	 * @return A handle to stop following the file.
	 */
	public static Closeable followTail(final File f,
			final Performer<String> newLineCallback) throws IOException {
		final FileInputStream fin = new FileInputStream(f);
		final FileChannel fc = fin.getChannel();

		ExecutorService thread = Executors.newSingleThreadExecutor();
		thread.submit(new Runnable() {
			public void run() {
				try {
					final long fileLength = f.length();
					fc.position(fileLength);

					for (ByteBuffer buf = ByteBuffer.allocateDirect(1024); fc
							.isOpen()
							&& !Thread.currentThread().isInterrupted();) {
						int read = fc.read(buf);
						if (read > 0) {
							byte[] b = new byte[read];
							buf.flip();
							buf.get(b);
							newLineCallback.invoke(String.format("%s",
									new String(b)));
							buf.clear();
						} else {
							try {
								Thread.sleep(10L);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		return new Closeable() {
			@Override
			public void close() throws IOException {
				fin.close();
			}
		};
	}

	public static final class Tail {
		public static void main(String[] args) throws IOException {
			if (args.length < 1) {
				System.err.printf("usage: java %s filename%n",
						Tail.class.getName());
				System.exit(1);
			}

			String filename = args[0];
			File file = new File(filename);
			if (!file.exists()) {
				throw new FileNotFoundException(file.getName());
			}
			followTail(file, new Performer<String>() {
				@Override
				public void perform(String val) {
					System.out.printf("%s", val);
				}
			});
		}
	}

	/**
	 * Writes the specified data to the specified file, overwriting an existing
	 * data.
	 * 
	 * @param f
	 *            The file to be written.
	 * @param data
	 *            The data to write.
	 * @throws IOException
	 *             If an I/O error occurred.
	 */
	public static final void writeDataToFile(File f, String data)
			throws IOException {
		try (FileWriter writer = new FileWriter(f)) {
			writer.write(data);
		}
	}
}
