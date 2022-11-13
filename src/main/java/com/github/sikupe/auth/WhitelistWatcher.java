package com.github.sikupe.auth;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.logging.Logger;

public class WhitelistWatcher {
    private static final Path WHITELIST_PATH = new File("whitelist.json").toPath();
    private final PathWatcher pathWatcher;
    private final Logger logger;

    public WhitelistWatcher(@NotNull Logger logger) {
        this.logger = logger;
        this.pathWatcher = PathWatcher.createPathWatcher(WHITELIST_PATH, this::handleWhitelistChange, 1_000);
    }

    private void handleWhitelistChange() {
        logger.info("Whitelist changed");
        logger.info("Reloading whitelist");
        Bukkit.reloadWhitelist();
        logger.info("Reloaded whitelist");
    }

    public void startWatchingAsync() {
        logger.info("Watching whitelist: " + WHITELIST_PATH.toAbsolutePath());
        this.pathWatcher.startWatchingAsync();
    }

    public void stopWatchingAsync() {
        this.pathWatcher.stopWatchingAsync();
    }
}
