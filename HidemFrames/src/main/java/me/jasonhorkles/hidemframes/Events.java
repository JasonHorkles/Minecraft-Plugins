package me.jasonhorkles.hidemframes;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public record Events(JavaPlugin plugin) implements CommandExecutor, Listener {
    private static final ArrayList<Player> hidingItemFrame = new ArrayList<>();
    private static final ArrayList<Player> dontAutoRemovePlayer = new ArrayList<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "hideitemframe" -> {
                if (!hidingItemFrame.contains(player)) {
                    player.sendMessage(Component.text(
                        "Place or rotate the item in the Item Frame that you'd like to make invisible.",
                        NamedTextColor.DARK_GREEN));

                    hidingItemFrame.add(player);
                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (hidingItemFrame.contains(player)) {
                                hidingItemFrame.remove(player);
                                player.sendMessage(
                                    Component.text("Item Frame hiding timed out.", NamedTextColor.RED));
                            }
                        }
                    };
                    task.runTaskLaterAsynchronously(plugin, 600);
                }
            }

            case "masshideitemframes" -> {
                if (!hidingItemFrame.contains(player)) hidingItemFrame.add(player);
                if (!dontAutoRemovePlayer.contains(player)) dontAutoRemovePlayer.add(player);

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        hidingItemFrame.remove(player);
                        dontAutoRemovePlayer.remove(player);
                        player.sendMessage(
                            Component.text("Mass Item Frame hiding disabled.", NamedTextColor.GRAY));
                    }
                };
                task.runTaskLaterAsynchronously(plugin, 300);

                player.sendMessage(Component.text(
                    "Place or rotate the items in the Item Frames that you'd like to make invisible.",
                    NamedTextColor.DARK_GREEN).append(
                    Component.text("\nMass Item Frame hiding is enabled for 15 seconds.",
                        NamedTextColor.DARK_AQUA)));
            }
        }
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrameInteract(PlayerItemFrameChangeEvent event) {
        if (event.getAction().equals(PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE)) {
            if (!event.getItemFrame().isVisible()) event.getItemFrame().setVisible(true);

        } else {
            Player player = event.getPlayer();
            if (!hidingItemFrame.contains(player)) return;

            if (event.getAction().equals(PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE))
                if (event.getItemFrame().isVisible()) event.setCancelled(true);

            event.getItemFrame().setVisible(false);
            if (!dontAutoRemovePlayer.contains(player)) hidingItemFrame.remove(player);
        }
    }
}
