package me.jasonhorkles.hardcore;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public record Revive(JavaPlugin plugin) implements Listener {
    @EventHandler
    public void onCharge(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.RESPAWN_ANCHOR) return;

        RespawnAnchor anchor = (RespawnAnchor) event.getClickedBlock().getBlockData();
        if (anchor.getCharges() == anchor.getMaximumCharges()) {
            event.setCancelled(true);

            int deadPlayers = 0;
            for (Player players : Bukkit.getOnlinePlayers())
                if (players.getGameMode() == GameMode.SPECTATOR) deadPlayers++;

            if (deadPlayers == 0) {
                event.getPlayer()
                    .sendMessage(Component.text("There are no dead players!").color(NamedTextColor.RED));
                return;
            }

            World world = event.getClickedBlock().getWorld();
            Location location = event.getClickedBlock().getLocation().toCenterLocation();
            Location lightningLoc = event.getClickedBlock().getLocation().toCenterLocation()
                .subtract(0, 0.5, 0);

            world.strikeLightningEffect(lightningLoc);
            world.setType(location, Material.AIR);
            world.playSound(location, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.MASTER, 5f, 1f);
            world.playSound(location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.MASTER, 5f, 1f);
            world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 5f, 0.9f);
            world.playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 5f, 0.9f);

            BukkitRunnable respawn = new BukkitRunnable() {
                @Override
                public void run() {
                    Player alive = null;
                    for (Player players : Bukkit.getOnlinePlayers())
                        if (players.getGameMode() != GameMode.SPECTATOR) {
                            alive = players;
                            break;
                        }

                    for (Player players : Bukkit.getOnlinePlayers())
                        if (players.getGameMode() == GameMode.SPECTATOR) {
                            if (alive != null) {
                                float yaw = alive.getLocation().getYaw();
                                if (yaw < 0) yaw += 180;
                                else if (yaw > 0) yaw -= 180;
                                location.setYaw(yaw);
                            } else location.setYaw(0);
                            location.setPitch(0);
                            players.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            players.setGameMode(GameMode.SURVIVAL);
                        }

                    world.playSound(location, Sound.ITEM_TOTEM_USE, SoundCategory.MASTER, 5f, 1f);
                    world.spawnParticle(Particle.TOTEM, location, 1000, -2f, 3f, -2f, 5f, null, true);
                    world.spawnParticle(Particle.CLOUD, location, 1000, 0f, 0f, 0f, 0.75f, null, true);
                }
            };
            respawn.runTaskLater(plugin, 60);
        } else if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GLOWSTONE) {
            event.setCancelled(true);
            event.getPlayer()
                .sendMessage(Component.text("The Anchor isn't fully charged!").color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        ArrayList<Player> alive = new ArrayList<>();
        ArrayList<Player> dead = new ArrayList<>();

        BukkitRunnable wait = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.getGameMode() == GameMode.SPECTATOR) dead.add(players);
                    else alive.add(players);

                if (alive.size() > 0) for (Player players : dead)
                    players.teleportAsync(alive.get(0).getLocation().add(0, 0.5, 0),
                        PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        };
        wait.runTaskLater(plugin, 5);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        for (Block blocks : event.blockList())
            if (blocks.getType() == Material.RESPAWN_ANCHOR) {
                event.setCancelled(true);
                for (Player players : Bukkit.getOnlinePlayers())
                    players.sendMessage(
                        Component.text("Cancelled a potential explosion bug!").color(NamedTextColor.RED));
                break;
            }
    }
}
