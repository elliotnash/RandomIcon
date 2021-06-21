package org.elliotnash.randomicon.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import static org.bukkit.Bukkit.getServer;
import static org.elliotnash.randomicon.spigot.RandomIcon.*;

public class SpigotPingListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerListPing(ServerListPingEvent event){
        if (!serverIcons.isEmpty())
            event.setServerIcon(serverIcons.get(rand.nextInt(serverIcons.size())));

        StringBuilder sb = new StringBuilder();
        for (Player player : getServer().getOnlinePlayers()){
            sb.append(player.getName()).append(", ");
        }
        if (sb.length()>2) {
            sb.setLength(sb.length() - 2);
        } else {
            sb.append("No players online");
        }
        String players = sb.toString();
        if (getLength(players)>269){
            int onlinePlayers = getServer().getOnlinePlayers().size();
            if (onlinePlayers!=1)
                players = (getServer().getOnlinePlayers().size()+" players online");
            else players = (getServer().getOnlinePlayers().size()+" player online");
        }

        event.setMotd(center("§x§5§5§5§5§a§5There is one imposter among us")+"\n"+center(players));
    }
}
