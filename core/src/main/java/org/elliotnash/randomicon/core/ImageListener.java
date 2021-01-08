package org.elliotnash.randomicon.core;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageListener {
    //no documentation :]
    void onMessageReceived(List<BufferedImage> images);
    void onMessageDeleted();
    void onLoad(List<BufferedImage> images);
}
