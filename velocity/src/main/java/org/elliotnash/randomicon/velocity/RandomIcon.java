package org.elliotnash.randomicon.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "randomicon-velocity",
        name = "RandomIcon-Velocity",
        description = "Sets the server icon to a random Icon from a folder each ping",
        version = "1.0-SNAPSHOT",
        authors = {"Elliot Nash"}
)
public class RandomIcon {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }

    //TODO add update checker for velocity

}
