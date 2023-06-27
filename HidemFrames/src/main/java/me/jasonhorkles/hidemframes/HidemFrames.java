package me.jasonhorkles.hidemframes;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class HidemFrames extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getCommand("hideitemframe").setExecutor(new Events(this));
        getCommand("masshideitemframes").setExecutor(new Events(this));

        getServer().getPluginManager().registerEvents(new Events(this), this);
    }
}
