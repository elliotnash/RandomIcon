package org.elliotnash.randomicon.spigot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.LinkedList;
import java.util.stream.Collectors;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.elliotnash.randomicon.core.DiscordLoader;
import org.elliotnash.randomicon.core.FileLoader;
import org.elliotnash.randomicon.core.ImageListener;
import org.elliotnash.randomicon.core.ImageLoader;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;

public final class SpigotMain extends JavaPlugin implements Listener, ImageListener {
    public static List<CachedServerIcon> serverIcons = new LinkedList<>();
    public static SpigotMain plugin;
    public static BukkitAudiences bukkitAudiences;
    public static ImageLoader imageLoader;
    public static FileConfiguration config;
    Random rand = new Random();

    @Override
    public void onEnable() {

        plugin = this;
        bukkitAudiences = BukkitAudiences.create(plugin);
        config = getConfig();

        if (!config.contains("discord")){
            saveDefaultConfig();
            config = getConfig();
        }

        boolean useDiscord = config.getBoolean("useDiscord");
        String token = config.getString("token");
        String channelId = config.getString("channelid");

        if (useDiscord) {
            try {
                imageLoader = new DiscordLoader(this, token, channelId, getDataFolder().toString() + "/tmpicon");
            } catch (LoginException e) {
                getLogger().info("Discord token was invalid, defaulting to file storage");
                imageLoader = new FileLoader(this, getDataFolder().toString() + "/tmpicon");
            }
        } else {
            imageLoader = new FileLoader(this, getDataFolder().toString() + "/tmpicon");
        }

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

    @Override
    public void onMessageReceived(List<BufferedImage> images) {
        serverIcons.addAll(images.stream().map(image -> {
            try {
                return getServer().loadServerIcon(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()));
    }

    @Override
    public void onMessageDeleted() {
    }

    @Override
    public void onLoad(List<BufferedImage> images) {
        serverIcons = images.stream().map(image -> {
            try {
                return getServer().loadServerIcon(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }
}
