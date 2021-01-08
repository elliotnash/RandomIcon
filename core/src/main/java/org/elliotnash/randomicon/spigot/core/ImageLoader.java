package org.elliotnash.randomicon.spigot.core;

import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.util.List;

//public class ImageLoader {
//    public static void main(String[] args) throws LoginException, InterruptedException {
//        Loader loader = new DiscordLoader("NzY2NDc0ODAyNzM1MDg3NjM2.X4j5SQ.srd2Uq_PUZNYU9nkfqcXJr1vjIE", "796624115951206412", "downloads");
//        Thread.sleep(10000);
//        System.out.println(loader.getImages());
//        ImageLoader hi = new ImageLoader();
//        hi.onMessage()
//    }
//
//    public void onMessage()
//
//}

public interface ImageLoader {
    List<BufferedImage> getImages();
}
