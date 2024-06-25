package me.jasonhorkles.nosleep;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class NoSleep extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getCommand("nosleep").setExecutor(new Events(this));
        getCommand("allowsleep").setExecutor(new Events(this));

        getServer().getPluginManager().registerEvents(new Events(this), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Events.preventSleep) if (Bukkit.getWorld("survival").isDayTime()) {
                    Events.preventSleep = false;
                    Events.playerPreventing = null;
                    getLogger().info("Sleep has been allowed again.");
                }
            }
        }.runTaskTimer(this, 0, 20 * 30);
    }
}
