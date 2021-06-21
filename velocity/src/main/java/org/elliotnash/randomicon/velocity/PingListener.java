package org.elliotnash.randomicon.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.elliotnash.randomicon.core.DefaultFontInfo;

import java.util.Random;

import static org.elliotnash.randomicon.velocity.RandomIcon.serverIcons;

public class PingListener {
    Random rand = new Random();
    @Subscribe
    public void onProxyPing(ProxyPingEvent event){
        ServerPing.Builder serverPingBuilder = ServerPing.builder();
        if (!serverIcons.isEmpty())
            serverPingBuilder.favicon(serverIcons.get(rand.nextInt(serverIcons.size())));

        StringBuilder sb = new StringBuilder();
        for (Player player : RandomIcon.server.getAllPlayers()){
            sb.append(player.getUsername()).append(", ");
        }
        if (sb.length()>2) {
            sb.setLength(sb.length() - 2);
        } else {
            sb.append("No players online");
        }
        String players = sb.toString();
        if (DefaultFontInfo.getStringLength(players)>269){
            int onlinePlayers = RandomIcon.server.getAllPlayers().size();
            if (onlinePlayers!=1)
                players = (onlinePlayers+" players online");
            else players = (onlinePlayers+" player online");
        }

        serverPingBuilder.description(Component.text()
                .append(Component.text(DefaultFontInfo.center("There is one imposter among us"))
                        .color(TextColor.color(0x5555a5)))
                .append(Component.text("\n"+DefaultFontInfo.center(players))).build());

        event.setPing(serverPingBuilder.build());
    }
}
