package org.elliotnash.randomicon.core;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DiscordLoader extends ListenerAdapter implements ImageLoader {

    public List<File> images = new LinkedList<File>();
    private final String channelid;
    private final String downloadPath;
    //callbacks for event
    private final ImageListener listener;
    private final JDA jda;

    public void getImages(){
        listener.onLoad(images.stream().map(image -> {
            try {
                return ImageIO.read(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()));
    }

    public DiscordLoader(ImageListener listener, String token, String channelid, String downloadPath) throws LoginException {
        File file = new File(downloadPath);
        if (!file.isDirectory()){
            file.mkdirs();
        }

        this.channelid = channelid;
        this.downloadPath = downloadPath;
        this.listener = listener;
        jda = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).build();
        jda.addEventListener(this);
    }
    public void shutdown(){
        jda.shutdown();
    }

    @Override
    public void onReady(ReadyEvent event){
        TextChannel channel = jda.getTextChannelById(channelid);
        MessageHistory history = channel.getHistoryFromBeginning(100).complete();
        for (Message message : history.getRetrievedHistory()){
            List<File> messageAtt = is64(message.getAttachments());
            images.addAll(messageAtt);
            if (messageAtt.isEmpty()){
                message.delete().queue();
            }
        }

        //dispatch on load event
        getImages();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        if(event.getChannel().getId().equals(channelid)){
            List<File> messageAtt = is64(event.getMessage().getAttachments());
            images.addAll(messageAtt);
            if (messageAtt.isEmpty()){
                event.getMessage().delete().queue();
            } else {
                listener.onMessageReceived(messageAtt.stream().map(image -> {
                    try {
                        return ImageIO.read(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList()));
            }
        }
    }

    public List<File> is64(List<Message.Attachment> attachments){
        LinkedList<File> files = new LinkedList<>();
        for (Message.Attachment att : attachments){
            if (att.isImage()){
                if (att.getWidth() == 64 && att.getHeight() == 64 && att.getFileExtension().equals("png")) {
                    try {
                        File file = att.downloadToFile(downloadPath+"/"+att.getFileName()).get();
                        files.add(file);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new ArrayList<File>();
                }
            }
        }
        return files;
    }

//    public boolean containsImage(List<File> files, Message.Attachment att) {
//        String checkName = att.getFileName();
//        int checkSize = att.getSize();
//        for (File file : files){
//            if (file.getName().equals(checkName) && file.length() == checkSize)
//                return true;
//        }
//        return false;
//    }



}