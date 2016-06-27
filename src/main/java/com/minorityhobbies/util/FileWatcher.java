/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A generic file watcher that enables directories to be watched for files of
 * interest.
 *
 * @author chrisw@minorityhobbies.com (Chris Wraith)
 */
public class FileWatcher implements Closeable {
    public static final long DEFAULT_CHECK_FREQUENCY_MILLIS = 500L;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final ExecutorService thread = Executors.newSingleThreadExecutor();

    private final File directory;
    private final FileListener fileListener;
    private final FilenameFilter defaultFilter;
    private final long checkFrequencyMillis;

    private FilenameFilter filter;

    private static final class FileAttributes {
        final long size;
        final long lastModified;
        boolean changeFlag;
        long changeLastSeen;

        public FileAttributes(long size, long lastModified) {
            super();
            this.size = size;
            this.lastModified = lastModified;
        }
    }

    public FileWatcher(File directory, FileListener listener, long checkFrequency, TimeUnit unit) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException(String.format(
                    "Directory '%s' does not exist or is a file.", directory));
        }
        this.directory = directory;
        this.defaultFilter = (dir, name) -> !new File(dir, name).isDirectory();
        this.filter = defaultFilter;
        this.fileListener = listener;
        this.checkFrequencyMillis = TimeUnit.MILLISECONDS.convert(checkFrequency, unit);
        thread.submit(this::watch);
    }

    public FileWatcher(File directory, FileListener listener) {
        this(directory, listener, DEFAULT_CHECK_FREQUENCY_MILLIS, TimeUnit.MILLISECONDS);
    }

    public FileWatcher(String directory, FileListener listener) {
        this(new File(directory), listener);
    }

    void watch() {
        logger.info(String.format("Starting file watcher for directory: %s",
                directory));

        Map<File, FileAttributes> cache = new HashMap<>();
        List<File> files = Arrays.asList(directory.listFiles(filter));

        populateCache(cache, files);

        while (!Thread.currentThread().isInterrupted()) {
            for (File file : files) {
                if (cache.containsKey(file)) {
                    FileAttributes cachedFileAttr = cache.get(file);
                    if (cachedFileAttr.size != file.length()
                            || cachedFileAttr.lastModified != file.lastModified() || cachedFileAttr.changeFlag) {
                        try {
                            if (cachedFileAttr.changeFlag
                                    && (System.currentTimeMillis() - cachedFileAttr.changeLastSeen) >= 2000L) {
                                cachedFileAttr.changeFlag = false;
                                // updated:
                                logger.info(String.format("File '%s' was updated", file.getAbsolutePath()));
                                fileListener.processFile(file, FileListener.FileListenerEventType.UPDATED);
                            } else if (!cachedFileAttr.changeFlag) {
                                cachedFileAttr.changeFlag = true;
                                cachedFileAttr.changeLastSeen = System.currentTimeMillis();
                            }
                        } catch (Exception e) {
                            logger.severe(String.format(
                                    "Error processing file '%s': %s%n%s", file, e.getMessage(), ExceptionUtilities.getStackTraceAsString(e)));
                        }
                    }
                } else {
                    // created:
                    logger.info(String.format("File '%s' was created", file.getAbsolutePath()));
                    fileListener.processFile(file, FileListener.FileListenerEventType.CREATED);
                }
            }

            // some files may have been deleted so try to find them
            List<File> deleteCandidates = new LinkedList<>(files);
            cache.keySet().stream()
                    .filter(cachedFile -> !deleteCandidates.contains(cachedFile))
                    .forEach(cachedFile -> {
                        fileListener.processFile(cachedFile,
                                FileListener.FileListenerEventType.DELETED);
                        logger.info(String.format("File '%s' was deleted", cachedFile.getAbsolutePath()));
                    });

            populateCache(cache, files);
            try {
                Thread.sleep(checkFrequencyMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            files = Arrays.asList(directory.listFiles());
        }
    }

    private void populateCache(Map<File, FileAttributes> cache, List<File> files) {
        cache.clear();
        files.stream()
                .forEach(file -> cache.put(file, new FileAttributes(file.length(), file.lastModified())));
    }

    @Override
    public void close() throws IOException {
        if (thread != null) {
            thread.shutdownNow();
        }
    }

    public final void setFilter(final FilenameFilter filter) {
        this.filter = (dir, name) -> defaultFilter.accept(dir, name)
                && filter.accept(dir, name);
    }
}
