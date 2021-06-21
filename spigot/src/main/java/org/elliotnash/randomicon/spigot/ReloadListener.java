package org.elliotnash.randomicon.spigot;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.elliotnash.randomicon.spigot.RandomIcon.*;

public class ReloadListener implements CommandExecutor {
    TextComponent reloadSuccess = Component.text("Config successfully reloaded").color(TextColor.color(0x6565a6));
    TextComponent noValidImages = Component.text("No valid image files provided").color(TextColor.color(0xEE1010)).append(
            Component.text("\nImages must be exactly 64x64 and in png format").color(TextColor.color(0xb5b5f5)));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("rip.reload")) {
            imageLoader.getImages();

            Audience audience;
            if (sender instanceof Player)
                audience = bukkitAudiences.player((Player) sender);
            else
                audience = bukkitAudiences.console();

            if(!serverIcons.isEmpty()){
                audience.sendMessage(reloadSuccess);
            } else {
                audience.sendMessage(noValidImages);
            }

        }
        return true;
    }
}
