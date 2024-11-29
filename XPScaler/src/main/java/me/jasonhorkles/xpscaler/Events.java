package me.jasonhorkles.xpscaler;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Events implements Listener {
    public Events(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        setSize(event.getPlayer());
    }

    @EventHandler
    public void onXpChange(PlayerExpChangeEvent event) {
        setSize(event.getPlayer());
    }

    @EventHandler
    public void onAnvil(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.PLAYER) setSize((Player) event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        setSize(event.getPlayer());
    }

    // The below methods are from https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/com/earth2me/essentials/craftbukkit/SetExpFix.java

    private void setSize(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                int xp = getTotalExperience(player);

                double newScale;
                // 550 = level 20
                // 1,395 = level 30
                if (xp <= 550) newScale = 0.25 + (0.25 / 550) * xp;
                else if (xp <= 1395) newScale = 0.5 + (0.5 / (1395 - 550)) * (xp - 550);
                else {
                    double growthFactor = 0.0005; // Adjust as needed for post-1395 scaling
                    newScale = Math.min(1.0 + growthFactor * (xp - 1395), 2.0);
                }

                AttributeInstance scale = player.getAttribute(Attribute.SCALE);
                if (scale == null) return;
                scale.setBaseValue(newScale);
            }
        }.runTask(plugin);
    }

    private static int getExpAtLevel(final Player player) {
        return getExpAtLevel(player.getLevel());
    }

    public static int getExpAtLevel(final int level) {
        if (level <= 15) return (2 * level) + 7;
        if (level <= 30) return (5 * level) - 38;
        return (9 * level) - 158;
    }

    //This method is required because the bukkit player.getTotalExperience() method shows all exp that has been gained but not spent.
    public static int getTotalExperience(final Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) exp = Integer.MAX_VALUE;
        return exp;
    }
}
