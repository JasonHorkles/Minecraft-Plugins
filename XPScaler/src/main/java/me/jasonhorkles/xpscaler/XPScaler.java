package me.jasonhorkles.xpscaler;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused"})
public class XPScaler extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }
}
