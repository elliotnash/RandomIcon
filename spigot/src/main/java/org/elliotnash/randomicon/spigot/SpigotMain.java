package org.elliotnash.randomicon.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.util.Random;
import java.util.LinkedList;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public final class SpigotMain extends JavaPlugin implements Listener {
    public static LinkedList<CachedServerIcon> serverIcons;
    public static SpigotMain plugin;
    public static BukkitAudiences bukkitAudiences;
    Random rand = new Random();

    @Override
    public void onEnable() {

        plugin = this;
        bukkitAudiences = BukkitAudiences.create(plugin);

        //serverIcons = getFavicons();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getCommand("rireload").setExecutor(new ReloadListener());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    MinecraftFont font = new MinecraftFont();
    String center(String str){

        int pixelsToAdd = 269-getLength(str);

        String spacer = " ";
        spacer = spacer.repeat((pixelsToAdd/2)/4);
        return spacer+str+"§r";
    }

    int getLength(String str){
        String noFormat = str.replaceAll("(§[0-9a-fA-FxXkKmMoOlLnNrR])+", "");
        return font.getWidth(noFormat);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerListPing(ServerListPingEvent event){
        if (!serverIcons.isEmpty())
            event.setServerIcon(serverIcons.get(rand.nextInt(serverIcons.size())));

        StringBuilder sb = new StringBuilder();
        for (Player player : getServer().getOnlinePlayers()){
            sb.append(player.getName()+", ");
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
