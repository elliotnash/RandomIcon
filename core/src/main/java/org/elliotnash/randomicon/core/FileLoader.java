package org.elliotnash.randomicon.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileLoader implements ImageLoader{
    private final File dir;
    private final ImageListener listener;
    public FileLoader(ImageListener listener, String path) {
        dir = new File(path);
        if (!dir.isDirectory()){
            dir.mkdirs();
        }
        this.listener = listener;
    }
    @Override
    public void getImages() {

        File[] directoryListing = dir.listFiles();
        LinkedList<BufferedImage> favicons = new LinkedList<>();
        if (directoryListing != null) {
            for (File f : directoryListing) {
                if(f.getName().contains(".png")){
                    try {
                        BufferedImage i = ImageIO.read(new File(dir + File.separator + f.getName()));
                        int width = i.getWidth();
                        int height = i.getHeight();
                        if(width == 64 && height == 64){
                            favicons.add(i);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }else{
            dir.mkdirs();
        }
        listener.onLoad(favicons);
    }
}
