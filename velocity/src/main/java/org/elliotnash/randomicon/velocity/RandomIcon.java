package org.elliotnash.randomicon.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.Favicon;
import org.elliotnash.randomicon.core.DiscordLoader;
import org.elliotnash.randomicon.core.FileLoader;
import org.elliotnash.randomicon.core.ImageListener;
import org.elliotnash.randomicon.core.ImageLoader;
import org.elliotnash.randomicon.core.config.ConfigManager;

import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Plugin(
        id = "randomicon-velocity",
        name = "RandomIcon-Velocity",
        description = "Sets the server icon to a random Icon from a folder each ping",
        version = "1.0-SNAPSHOT",
        authors = {"Elliot Nash"}
)
public class RandomIcon implements ImageListener {

    public static ProxyServer server;
    public static Logger logger;
    public static Path dataDirectory;
    public static List<Favicon> serverIcons = new LinkedList<>();
    public static ImageLoader imageLoader;
    public static ConfigManager config;

    @Inject
    private RandomIcon(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory){
        RandomIcon.server = server;
        RandomIcon.logger = logger;
        RandomIcon.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        config = new ConfigManager(dataDirectory+"/config.toml");
        config.read();

        if (config.getUseDiscord()) {
            try {
                imageLoader = new DiscordLoader(this, config.getToken(), config.getChannel(),
                        dataDirectory + "/tmpicon");
            } catch (LoginException e) {
                logger.info("Discord token was invalid, defaulting to file storage");
                imageLoader = new FileLoader(this, dataDirectory + "/serverIcons");

            }
        } else {
            imageLoader = new FileLoader(this, dataDirectory + "/serverIcons");
        }

        server.getEventManager().register(this, new PingListener());
        server.getCommandManager().register("rireload", new ReloadListener());

    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (imageLoader instanceof DiscordLoader){
            ((DiscordLoader) imageLoader).shutdown();
        }
    }

    //TODO add update checker for velocity

    @Override
    public void onMessageReceived(List<BufferedImage> images) {
        serverIcons.addAll(images.stream().map(Favicon::create).collect(Collectors.toList()));
    }

    @Override
    public void onMessageDeleted() {
    }

    @Override
    public void onLoad(List<BufferedImage> images) {
        serverIcons = images.stream().map(Favicon::create).collect(Collectors.toList());
    }

}
