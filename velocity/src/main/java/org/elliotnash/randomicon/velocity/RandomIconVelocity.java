package org.elliotnash.randomicon.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "RandomIcon-Velocity",
        name = "RandomIcon-Velocity",
        version = "1.0-SNAPSHOT",
        authors = {"Elliot"}
)
public class RandomIconVelocity {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
