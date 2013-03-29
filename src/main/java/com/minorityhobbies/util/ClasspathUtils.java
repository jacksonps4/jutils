/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Utilities that make use of the classpath. This provides some handy methods
 * for reading files in a non environment specific way.
 * 
 */
public class ClasspathUtils {
	private static final ClassLoader CLASSLOADER = ClasspathUtils.class.getClassLoader();
	
	private ClasspathUtils() {
	}

	/**
	 * Reads a single file from the classpath and returns its contents.
	 * 
	 * @param path
	 *            The path relative to the classpath from which to read a file.
	 * @return The contents of the specified file or null if the file was not
	 *         found.
	 * @throws IOException
	 *             If the file was found but an I/O error occurred reading the
	 *             file.
	 */
	public static String readFile(String path) throws IOException {
		InputStream in = null;
		try {
			in = CLASSLOADER.getResourceAsStream(path);
			if (in == null) {
				return null;
			}
			byte[] b = new byte[1024 * 64];
			StringBuilder fileData = new StringBuilder();
			for (int read = 0; (read = in.read(b)) > 0;) {
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
	 * Reads a list of files from the specified path. The path can either be a
	 * directory or a filename. If a directory is specified, this method will
	 * return a list of all of the files in that directory (but not its
	 * sub-directories). If a filename is specified, this method will return a
	 * list of a single file.
	 * 
	 * @param path
	 *            The path relative to the classpath from which to a file or
	 *            files.
	 * @return The files read
	 * @throws IOException
	 *             If an I/O error occurred reading the files.
	 */
	public static List<File> readFiles(String path) throws IOException {
		List<File> files = new ArrayList<File>();
		URL resourceUrl = CLASSLOADER.getResource(path);
		if (resourceUrl == null) {
			throw new FileNotFoundException(path);
		}
		try {
			File resourceFile = new File(resourceUrl.toURI());
			if (resourceFile.isDirectory()) {
				for (File f : resourceFile.listFiles()) {
					files.add(f);
				}
			} else {
				files.add(resourceFile);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return files;
	}

	/**
	 * Reads a properties file from the classpath.
	 * 
	 * @param relativePath
	 *            The path relative to the root of the classpath where this
	 *            properties file can be found.
	 * @return The translated properties or null if the file is not found.
	 * @throws IOException
	 *             If an I/O error occurred reading the file.
	 */
	public static Properties readProperties(String relativePath)
			throws IOException {
		Properties properties = new Properties();
		InputStream in = CLASSLOADER.getResourceAsStream(relativePath);
		if (in == null) {
			return null;
		}
		properties.load(in);
		return properties;
	}

	/**
	 * Finds the absolute path to a file relative to the classpath.
	 * 
	 * @param path
	 *            The classpath relative path.
	 * @return The {@link File} which represents the abstract path name of the
	 *         specified classpath relative file.
	 */
	public static File getClasspathRelativePath(String path) {
		URL url = CLASSLOADER.getResource(path);
		if (url == null) {
			return null;
		}
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException();
		}
	}
}
