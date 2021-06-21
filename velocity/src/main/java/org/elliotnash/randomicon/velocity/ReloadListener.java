package org.elliotnash.randomicon.velocity;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import static org.elliotnash.randomicon.velocity.RandomIcon.imageLoader;
import static org.elliotnash.randomicon.velocity.RandomIcon.serverIcons;

public class ReloadListener implements SimpleCommand {
    TextComponent reloadSuccess = Component.text("Config successfully reloaded").color(TextColor.color(0x6565a6));
    TextComponent noValidImages = Component.text("No valid image files provided").color(TextColor.color(0xEE1010)).append(
            Component.text("\nImages must be exactly 64x64 and in png format").color(TextColor.color(0xb5b5f5)));

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source().hasPermission("ri.reload")) {
            imageLoader.getImages();
            invocation.source().sendMessage(serverIcons.isEmpty() ? noValidImages : reloadSuccess);
        }
    }

}
