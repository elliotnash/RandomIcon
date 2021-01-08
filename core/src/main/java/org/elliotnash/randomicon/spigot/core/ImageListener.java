package org.elliotnash.randomicon.spigot.core;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageListener {
    //no documentation :]
    void onMessageReceived();
    void onMessageDeleted();
    void onLoad(List<BufferedImage> images);
}
