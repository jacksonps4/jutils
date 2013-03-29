/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A generic file watcher that enables directories to be watched for files of
 * interest.
 * 
 * @author chris@minorityhobbies.com (Chris Wraith)
 * 
 */
public class FileWatcher implements Runnable {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final File directory;
	private final FileListener fileListener;

	private final FilenameFilter defaultFilter;
	private FilenameFilter filter;
	private Map<File, Long> cache = new HashMap<File, Long>();
	private Set<File> processed = new HashSet<File>();

	public FileWatcher(String directory, FileListener listener) {
		this.directory = new File(directory);
		if (!this.directory.exists()) {
			throw new IllegalArgumentException(String.format(
					"Directory '%s' does not exist.", directory));
		}
		this.defaultFilter = new FilenameFilter() {
			@Override
			public final boolean accept(File dir, String name) {
				File file = new File(dir, name);
				return !file.isDirectory();
			}
		};
		this.filter = defaultFilter;
		this.fileListener = listener;
	}

	@Override
	public void run() {
		logger.info(String.format("Starting file watcher for directory: %s",
				directory));

		while (!Thread.currentThread().isInterrupted()) {
			File[] files = directory.listFiles(filter);
			if (cache.size() > 0) {
				for (File file : files) {
					if (cache.containsKey(file)) {
						Long cachedFileSize = cache.get(file);
						if (cachedFileSize.longValue() == file.length()
								&& !processed.contains(file)) {
							try {
								logger.info(String.format("Processing file '%s'", file.getAbsolutePath()));
								fileListener.processFile(file);
								processed.add(file);
							} catch (Throwable t) {
								logger.severe(String.format(
										"Error processing file '%s': %s", file, t.getMessage()));
							}
						}
					}
				}
			}

			cache = new HashMap<File, Long>();
			for (File file : files) {
				cache.put(file, file.length());
			}
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public final void setFilter(final FilenameFilter filter) {
		this.filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return defaultFilter.accept(dir, name)
						&& filter.accept(dir, name);
			}
		};
	}
}
