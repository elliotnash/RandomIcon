package org.elliotnash.randomicon.spigot.core;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DiscordLoader extends ListenerAdapter {

    public List<File> images = new LinkedList<File>();
    private final String channelid;

    public DiscordLoader(String token, String channelid) throws LoginException {
        this.channelid = channelid;
        JDA jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(this);
    }

    @Override
    public void onReady(ReadyEvent event){
        TextChannel channel = event.getJDA().getTextChannelById(channelid);
        MessageHistory history = channel.getHistoryFromBeginning(100).complete();
        for (Message message : history.getRetrievedHistory()){
            List<File> messageAtt = is64(message.getAttachments());
            images.addAll(messageAtt);
            if (messageAtt.isEmpty()){
                message.delete().queue();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getChannel().getId().equals(channelid)){
            List<File> messageAtt = is64(event.getMessage().getAttachments());
            images.addAll(messageAtt);
            if (messageAtt.isEmpty()){
                event.getMessage().delete().queue();
            }
        }
    }

    public List<File> is64(List<Message.Attachment> attachments){
        LinkedList<File> files = new LinkedList<>();
        for (Message.Attachment att : attachments){
            if (att.isImage()){
                if (att.getWidth() == 64 && att.getHeight() == 64 && att.getFileExtension().equals("png")) {
                    try {
                        File file = att.downloadToFile("downloads/"+att.getFileName()).get();
                        if(!containsImage(images, att))
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

    public boolean containsImage(List<File> files, Message.Attachment att) {
        String checkName = att.getFileName();
        int checkSize = att.getSize();
        for (File file : files){
            if (file.getName().equals(checkName) && file.length() == checkSize)
                return true;
        }
        return false;
    }



}