package me.jasonhorkles.hardcore;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused"})
public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new Revive(this), this);
    }
}
