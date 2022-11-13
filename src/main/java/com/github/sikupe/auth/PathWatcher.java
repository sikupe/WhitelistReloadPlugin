package com.github.sikupe.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PathWatcher {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Path path;
    private final Runnable onChange;
    private final long interval;
    private Future<?> asyncWatcher;

    private long lastModified;
    private boolean existing;


    private PathWatcher(Path path, Runnable onChange, long interval, long lastModified, boolean existing) {
        this.path = path;
        this.onChange = onChange;
        this.lastModified = lastModified;
        this.existing = existing;
        this.interval = interval;
    }

    /**
     * Creates a new PathWatcher
     * @param path Path to watch (no recursive watching!)
     * @param onChange Callback which is called on a change (CREATE, MODIFY, DELETE)
     * @param interval Interval for querying in milliseconds
     * @return PatchWatcher
     */
    public static PathWatcher createPathWatcher(Path path, Runnable onChange, long interval) {
        boolean existing = Files.exists(path);
        long lastModified = readLastModified(path).orElse(-1L);
        return new PathWatcher(path, onChange, interval, lastModified, existing);
    }

    private static Optional<Long> readLastModified(Path path) {
        try {
            var attributes = Files.readAttributes(path, BasicFileAttributes.class);
            var lastModified = attributes.lastModifiedTime();
            return Optional.of(lastModified.toMillis());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void detectChange() {
        var existing = Files.exists(path);
        var lastModified = readLastModified(path);
        if (existing != this.existing) {
            this.onChange.run();
        } else if (lastModified.isPresent()) {
            if (lastModified.get() > this.lastModified) {
                this.onChange.run();
            }
        }
        this.existing = existing;
        lastModified.ifPresent((lm) -> this.lastModified = lm);
    }

    public void startWatchingAsync() {
        this.asyncWatcher = executorService.scheduleAtFixedRate(this::detectChange, 0L, interval, TimeUnit.MILLISECONDS);
    }

    public void stopWatchingAsync() {
        this.asyncWatcher.cancel(true);
    }
}
