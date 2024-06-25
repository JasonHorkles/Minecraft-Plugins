package me.jasonhorkles.nosleep;

import org.bukkit.Bukkit;
import org.bukkit.World;
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
                World world = Bukkit.getWorld("survival");
                if (world.getTime() >= 23500 && world.getTime() <= 23530) if (Events.preventSleep) {
                    Events.preventSleep = false;
                    Events.playerPreventing = null;
                    getLogger().info("Sleep has been allowed again.");
                }
            }
        }.runTaskTimer(this, 0, 20);
    }
}
