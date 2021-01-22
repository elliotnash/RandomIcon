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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.elliotnash.JenkinsUpdater;
import org.elliotnash.randomicon.core.DiscordLoader;
import org.elliotnash.randomicon.core.FileLoader;
import org.elliotnash.randomicon.core.ImageListener;
import org.elliotnash.randomicon.core.ImageLoader;
import org.elliotnash.randomicon.spigot.listeners.PaperPingListener;
import org.elliotnash.randomicon.spigot.listeners.SpigotPingListener;

import javax.security.auth.login.LoginException;

public final class SpigotMain extends JavaPlugin implements ImageListener {
    public static List<CachedServerIcon> serverIcons = new LinkedList<>();
    public static SpigotMain plugin;
    public static BukkitAudiences bukkitAudiences;
    public static ImageLoader imageLoader;
    public static FileConfiguration config;
    public static Logger logger;
    public static Random rand = new Random();

    @Override
    public void onEnable() {

        plugin = this;
        logger = getLogger();
        bukkitAudiences = BukkitAudiences.create(plugin);
        config = getConfig();

        updateCheck();

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
                imageLoader = new FileLoader(this, getDataFolder().toString() + "/serverIcons");
                
            }
        } else {
            imageLoader = new FileLoader(this, getDataFolder().toString() + "/serverIcons");
        }

        boolean isPapermc = false;
        try {
            isPapermc = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("Paper not detected. Please switch to papermc for better performance and improved plugin support");
            Bukkit.getLogger().info("https://papermc.io/");
        } if (isPapermc) {
            Bukkit.getLogger().info("Paper detected, loading support for paper's events");
            Bukkit.getServer().getPluginManager().registerEvents(new PaperPingListener(), this);
        } else {
            Bukkit.getServer().getPluginManager().registerEvents(new SpigotPingListener(), this);
        }

        getCommand("rireload").setExecutor(new ReloadListener());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void updateCheck(){
        new BukkitRunnable() {
            @Override
            public void run() {
                String version = plugin.getDescription().getVersion();
                JenkinsUpdater updater = new JenkinsUpdater("https://ci.elliotnash.org/job/Minecraft/job/RandomIcon", version);
                if (updater.shouldUpdate) {
                    int versionDiff = updater.latestVersion - updater.currentVersion;
                    if (versionDiff == 1)
                        logger.warning("RandomIcon is 1 version behind");
                    else
                        logger.warning("RandomIcon is " + versionDiff + " versions behind");
                    logger.warning("Please download a new build from https://ci.elliotnash.org/job/Minecraft/job/RandomIcon/");
                }
            }
        }.runTaskLaterAsynchronously(this, 100);
    }

    static MinecraftFont font = new MinecraftFont();
    public static String center(String str){

        int pixelsToAdd = 269-getLength(str);

        String spacer = " ";
        spacer = spacer.repeat((pixelsToAdd/2)/4);
        return spacer+str+"§r";
    }

    public static int getLength(String str){
        String noFormat = str.replaceAll("(§[0-9a-fA-FxXkKmMoOlLnNrR])+", "");
        return font.getWidth(noFormat);
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
