package com.minorityhobbies.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FileWatcherIT implements FileListener {
    private final Logger logger = Logger.getLogger("FileWatcherIT");
    private File dir;
    private FileWatcher fileWatcher;
    private List<FileChangeEvent> events;

    private static class FileChangeEvent {
        final File file;
        final FileListenerEventType eventType;

        public FileChangeEvent(File file, FileListenerEventType eventType) {
            this.file = file;
            this.eventType = eventType;
        }
    }



    @Before
    public void setUp() {
        events = new CopyOnWriteArrayList<>();

        dir = new File(System.getProperty("java.io.tmpdir"), "testdir");
        if (dir.listFiles() != null) {
            Arrays.asList(dir.listFiles()).stream()
                    .forEach(f -> f.delete());
            dir.delete();
        }
        if (!dir.mkdir()) {
            fail("Failed to created test dir: " + dir.getAbsolutePath());
        }

        fileWatcher = new FileWatcher(dir, this);
    }

    @After
    public void tearDown() {
        if (dir != null) {
            if (!dir.delete()) {
                logger.warning("Failed to delete test dir: " + dir.getAbsolutePath());
            }
        }
        if (fileWatcher != null) {
            try {
                fileWatcher.close();
            } catch (IOException e) {
                logger.warning("Failed to close FileWatcher");
            }
        }
    }

    @Override
    public void processFile(File file, FileListenerEventType eventType) {
        synchronized (this) {
            events.add(new FileChangeEvent(file, eventType));
            notifyAll();
        }
    }

    @Test
    public void noEventsAfterOneSeconds() throws InterruptedException {
        Thread.sleep(1000L);
        assertEquals(0, events.size());
    }

    @Test
    public void fileCreated() throws IOException, InterruptedException {
        File f = File.createTempFile("filewatchertest", ".txt", dir);
        try {
            awaitEvent("file creation", () -> events.size() > 0, 3, TimeUnit.SECONDS);
            FileChangeEvent event = events.get(0);
            assertEquals(f, event.file);
            assertEquals(FileListenerEventType.CREATED, event.eventType);
        } finally {
            if (!f.delete()) {
                logger.warning("Failed to delete file after test");
            }
        }
    }

    @Test
    public void fileUpdatedTouchDate() throws IOException, InterruptedException {
        Thread.sleep(500L);

        File f = File.createTempFile("filewatchertest", ".txt", dir);
        Thread.sleep(1000L);
        try {
            awaitEvent("file creation (update touch date)", () -> events.size() > 0, 3, TimeUnit.SECONDS);
            if (!f.setLastModified(System.currentTimeMillis())) {
                fail("Cannot touch file");
            }
            awaitEvent("file update", () -> events.size() > 1, 3, TimeUnit.SECONDS);
            FileChangeEvent event = events.get(1);
            assertEquals(f, event.file);
            assertEquals(FileListenerEventType.UPDATED, event.eventType);
        } finally {
            if (!f.delete()) {
                logger.warning("Failed to delete file after test");
            }
        }
    }

    @Test
    public void fileUpdatedSize() throws IOException, InterruptedException {
        Thread.sleep(500L);

        File f = File.createTempFile("filewatchertest", ".txt", dir);
        try {
            awaitEvent("file creation (update size)", () -> events.size() > 0, 3, TimeUnit.SECONDS);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
                writer.write("Test data");
            }
            awaitEvent("file update", () -> events.size() > 1, 3, TimeUnit.SECONDS);
            FileChangeEvent event = events.get(1);
            assertEquals(f, event.file);
            assertEquals(FileListenerEventType.UPDATED, event.eventType);
        } finally {
            if (!f.delete()) {
                logger.warning("Failed to delete file after test");
            }
        }
    }

    @Test
    public void fileDeleted() throws IOException, InterruptedException {
        Thread.sleep(500L);

        File f = File.createTempFile("filewatchertest", ".txt", dir);
        try {
            awaitEvent("file creation (delete)", () -> events.size() > 0, 3, TimeUnit.SECONDS);
            f.delete();
            awaitEvent("file delete", () -> events.size() > 1, 3, TimeUnit.SECONDS);
            FileChangeEvent event = events.get(1);
            assertEquals(f, event.file);
            assertEquals(FileListenerEventType.DELETED, event.eventType);
        } finally {
            f.delete();
        }
    }

    private void awaitEvent(String msg, Supplier<Boolean> waitCondition, long time, TimeUnit unit) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        while (!waitCondition.get()) {
            synchronized (this) {
                wait(TimeUnit.MILLISECONDS.convert(time, unit));
            }
            long timeWaited = System.currentTimeMillis() - startTime;
            if (timeWaited > TimeUnit.MILLISECONDS.convert(time, unit)) {
                throw new IllegalStateException("Timed out awaiting " + msg);
            }
        }
    }
}
