package com.github.sikupe.auth;

import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class WhitelistAuthPlugin extends JavaPlugin {
    private final WhitelistWatcher whitelistWatcher;

    public WhitelistAuthPlugin() {
        this.whitelistWatcher = new WhitelistWatcher(getLogger());
    }

    @Override
    public void onEnable() {
        getLogger().info("Start watching whitelist");
        this.whitelistWatcher.startWatchingAsync();
    }

    @Override
    public void onDisable() {
        getLogger().info("Stop watching whitelist");
        this.whitelistWatcher.stopWatchingAsync();
    }
}
