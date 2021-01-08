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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

        serverIcons = getFavicons();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getCommand("ripreload").setExecutor(new ReloadListener());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public LinkedList<CachedServerIcon> getFavicons(){
        File dir = new File(getDataFolder() + File.separator + "serverIcons");
        File[] directoryListing = dir.listFiles();
        LinkedList<CachedServerIcon> favicons = new LinkedList<>();
        if (directoryListing != null) {
            for (File f : directoryListing) {
                if(f.getName().contains(".png")){
                    try {
                        BufferedImage i = ImageIO.read(new File(dir + File.separator + f.getName()));
                        int width = i.getWidth();
                        int height = i.getHeight();
                        if(width == 64 && height == 64){
                            favicons.add(getServer().loadServerIcon(i));
                        }else{
                            if(!f.isHidden()){
                                getLogger().info("One of your server icons is not 64x64! It will not be used!");
                            }
                        }
                    }catch(IOException e){
                        getLogger().info("Something bad occured! Please report this to Ethemoose (RandomIconSpigot dev)");
                        getLogger().info("Include this, and the error below with the report");
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    if(!f.isHidden()){
                        getLogger().info("One of your server icons does not end in .png! It will not be used!");
                    }
                }
            }
        }else{
            if (!getDataFolder().isDirectory()){
                getDataFolder().mkdir();
            }
            getLogger().info("The serverIcons directory is missing! Trying to create one now..");
            dir.mkdir();
        }
        return favicons;
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
    public void onPaperServerListPing(ServerListPingEvent event){
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
