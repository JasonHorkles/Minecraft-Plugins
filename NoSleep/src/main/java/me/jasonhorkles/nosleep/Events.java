package me.jasonhorkles.nosleep;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record Events(JavaPlugin plugin) implements CommandExecutor, Listener {
    public static Player playerPreventing;
    public static boolean preventSleep = false;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "nosleep" -> {
                if (preventSleep) {
                    player.sendMessage(Component.text("Sleep is already being prevented.",
                        NamedTextColor.RED));
                    return true;
                }

                preventSleep = true;
                playerPreventing = player;
                for (Player players : plugin.getServer().getOnlinePlayers())
                    players.sendMessage(Component.text(player.getName() + " is preventing sleep.",
                        NamedTextColor.YELLOW));
            }

            case "allowsleep" -> {
                if (!preventSleep) {
                    player.sendMessage(Component.text("Sleep is not already being prevented!",
                        NamedTextColor.RED));
                    return true;
                }

                if (!player.equals(playerPreventing)) {
                    player.sendMessage(Component.text("You are not the one preventing sleep!",
                        NamedTextColor.RED));
                    return true;
                }

                preventSleep = false;
                for (Player players : plugin.getServer().getOnlinePlayers())
                    players.sendMessage(Component.text(player.getName() + " is no longer preventing sleep.",
                        NamedTextColor.GREEN));
            }
        }

        return true;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (preventSleep) {
            event.setCancelled(true);
            event.getPlayer().showTitle(Title.title(Component.text(
                    playerPreventing.getName(),
                    NamedTextColor.GOLD),
                Component.text("is preventing sleep", NamedTextColor.YELLOW)));
        }
    }
}