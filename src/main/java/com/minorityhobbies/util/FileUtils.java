package com.minorityhobbies.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
			for (int read = 0; (read = in.read(b)) > -1; ) {
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

		return listDirFilesRecursively(dir);
	}

	private static List<File> listDirFilesRecursively(File dir) {
		List<File> allFiles = new LinkedList<File>();
		File[] dirFiles = dir.listFiles();
		for (File dirFile : dirFiles) {
			if (dirFile.isDirectory()) {
				allFiles.addAll(listDirFilesRecursively(dirFile));
			} else {
				allFiles.add(dirFile);
			}
		}
		return allFiles;
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
				System.err.printf("usage: java %s filename%n", Tail.class.getName());
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
}
