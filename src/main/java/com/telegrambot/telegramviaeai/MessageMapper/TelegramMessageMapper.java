/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telegrambot.telegramviaeai.MessageMapper;

import com.telegrambot.telegramviaeai.LibaryConfig.TelegramViaeaiBot;
import static java.util.Objects.isNull;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Update;
import com.telegrambot.telegramviaeai.viaeaibotMessageTypes.MessageType;
import org.telegram.telegrambots.api.objects.File;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;




/**
 *
 * @author tomide
 */

//see https://github.com/rubenlagus/TelegramBots/wiki/Getting-Started
//https://github.com/SimonScholz/telegram-bot
//https://github.com/MonsterDeveloper/java-telegram-bot-tutorial


@Component
public class TelegramMessageMapper {
    
    @Autowired
    private TelegramViaeaiBot telegramViaeaiBot;



    public SendMessage mapToTelegramMessage(com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg){

        SendMessage message = new SendMessage();
        
        message.setChatId(msg.getCreatorId())
                .setText(msg.getBody());
        return message;
        
    }



    public SendPhoto mapToTelegramPhotoMessage(com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg){

        SendPhoto message = new SendPhoto();

        URL url=null;

        try {
            url = new URL(msg.getFileURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }



        try {
            message.setChatId(msg.getCreatorId())
                    .setNewPhoto(msg.getBody(),url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
        
    }


    public SendVideo mapToTelegramVideoMessage(com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg){

        SendVideo message = new SendVideo();

        URL url=null;

        try {
            url = new URL(msg.getFileURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }



        try {
            message.setChatId(msg.getCreatorId())
                    .setNewVideo(msg.getBody(),url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;

    }



    public SendAudio mapToTelegramAudioMessage(com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg){

        SendAudio message = new SendAudio();

        URL url=null;

        try {
            url = new URL(msg.getFileURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }



        try {
            message.setChatId(msg.getCreatorId())
                    .setNewAudio(msg.getBody(),url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;

    }

    public SendDocument mapToTelegramDocumentMessage(com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg){

        SendDocument message = new SendDocument();

        URL url=null;

        try {
            url = new URL(msg.getFileURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }



        try {
            message.setChatId(msg.getCreatorId())
                    .setNewDocument(msg.getBody(),url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;

    }

    /**
     * Parses message object in update and maps it to viaeaibotMessage.Message
     * @param update Update contains the message request to the bot from telegram.
     * @return returns viaeaibotMessage.Message
     */
    public com.telegrambot.telegramviaeai.viaeaibotMessage.Message maptoViaeaiMessage(Update update){

        com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg = new com.telegrambot.telegramviaeai.viaeaibotMessage.Message();

        if(update.hasMessage()){
            this.getUpdateMessage(update);
        }
        else if(update.hasChannelPost()){
            this.getChannelPostMessage(update);
        }

        return msg;
    }

    /**
     * This type of message is a regular message and not from a channelpost
     * @param update contains the message request
     * @return viaeaibotMessage.Message
     */
    public com.telegrambot.telegramviaeai.viaeaibotMessage.Message getUpdateMessage(Update update){
        
          com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg = new com.telegrambot.telegramviaeai.viaeaibotMessage.Message();
          

          
          if(update.hasMessage()){
              
             long id= update.getUpdateId();
             long creatorId= update.getMessage().getChatId();
             String time= update.getMessage().getDate().longValue()+"";

             boolean isBot=true;
             String body=null;
             boolean isFile=false;
             String fileUrl=null;

             MessageType type=MessageType.text;

             
             if(update.getMessage().getFrom().getId() <0){
                 isBot=true;
             }
             else{
                 isBot=false;
             }



              if(update.getMessage().hasText()){

                 body=update.getMessage().getText();
//
                 try{
                     URL url=new URL(body.trim());
                     type=MessageType.site;
                 }
                 catch (MalformedURLException ex){
                     type=MessageType.text;
                 }


             }
             else if(update.getMessage().hasPhoto()){
                 type =MessageType.image;
                 isFile=true;
                 String path = update.getMessage().getPhoto().stream().filter(pic->pic.hasFilePath()).findFirst().get().getFilePath();
                 fileUrl = File.getFileUrl(telegramViaeaiBot.getBotToken(), path);
                 //String.format("https://api.telegram.org/file/bot%s/%s",telegramViaeaiBot.getBotToken(),path);
             }
             else if(update.getMessage().hasDocument()){

                 type =MessageType.document;
                 isFile=true;
                 fileUrl=this.getFileUrl(update.getMessage().getDocument().getFileId());
                 body=update.getMessage().getCaption();

             }
             else if(!isNull(update.getMessage().getAudio()) || !isNull(update.getMessage().getVoice())){
                 type =MessageType.audio;
                 isFile=true;
                 body=update.getMessage().getCaption();

                 if(!isNull(update.getMessage().getAudio())){
                     fileUrl=this.getFileUrl(update.getMessage().getAudio().getFileId());
                 }
                 else {
                     fileUrl=this.getFileUrl(update.getMessage().getVoice().getFileId());
                 }

             }
             else if(!isNull(update.getMessage().getVideo()) || !isNull(update.getMessage().getVideoNote())){
                 type =MessageType.video;
                 isFile=true;
                 body=update.getMessage().getCaption();

                 if(!isNull(update.getMessage().getVideo())){
                     fileUrl=this.getFileUrl(update.getMessage().getVideo().getFileId());
                 }
                 else {
                     fileUrl=this.getFileUrl(update.getMessage().getVideoNote().getFileId());
                 }
             }
                
              msg.setCreatorId(creatorId)
                    .setMessageId(id)
                    .setMessage_time(time)
                    .setFileUrl(fileUrl)
                    .setIsFile(isFile)
                    .setMessageBody(body)
                    .setIsBot(isBot)
                    .setMessageType(type);

          }
            

       return msg;
          
    }
    
    /**
     * This method takes a fileId and returns the fileUrl
     * @param fileId
     * @return fileUrl
     */
    public String getFileUrl(String fileId){

        String url = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s",telegramViaeaiBot.getBotToken(),fileId);

        File file = new RestTemplate().getForObject(url,File.class);

        if(isNull(file)){
            return null;
        }

        return file.getFileUrl(telegramViaeaiBot.getBotToken());
    }

    
    
    /**
     * A message can either be received as regular message from users of channel post
     * @param update
     * @return 
     */
    public com.telegrambot.telegramviaeai.viaeaibotMessage.Message getChannelPostMessage(Update update){
        
        com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg = new com.telegrambot.telegramviaeai.viaeaibotMessage.Message();
          
          
          
          if(update.hasChannelPost()){
              
             long id= update.getUpdateId();
             long creatorId= update.getChannelPost().getChatId();
             String time= update.getChannelPost().getDate().longValue()+"";

             boolean isBot=true;
             String body=null;
             boolean isFile=false;
             String fileUrl=null;

             MessageType type=MessageType.text;


              if(update.getChannelPost().getFrom().getId() <0){
                  isBot=true;
              }
              else{
                  isBot=false;
              }


              if(update.getChannelPost().hasText()){

                 body=update.getChannelPost().getText();
//
                 try{
                     URL url=new URL(body.trim());
                     type=MessageType.site;
                 }
                 catch (MalformedURLException ex){
                     type=MessageType.text;
                 }


             }
             else if(update.getChannelPost().hasPhoto() || !isNull(update.getChannelPost().getNewChatPhoto()) ||
                     update.getChannelPost().getNewChatPhoto().size()>0){

                  type =MessageType.image;
                  isFile=true;

                 if(update.getChannelPost().hasPhoto()){
                     String path = update.getChannelPost().getPhoto().stream().filter(pic->pic.hasFilePath()).findFirst().get().getFilePath();
                     fileUrl = File.getFileUrl(telegramViaeaiBot.getBotToken(), path);
                 }
                 else{
                     String path = update.getChannelPost().getNewChatPhoto().stream().filter(pic->pic.hasFilePath()).findFirst().get().getFilePath();
                     fileUrl = File.getFileUrl(telegramViaeaiBot.getBotToken(), path);
                 }


                 //String.format("https://api.telegram.org/file/bot%s/%s",telegramViaeaiBot.getBotToken(),path);
             }
             else if(update.getChannelPost().hasDocument()){

                 type =MessageType.document;
                 isFile=true;
                 fileUrl=this.getFileUrl(update.getChannelPost().getDocument().getFileId());
                 body=update.getChannelPost().getCaption();

             }
             else if(!isNull(update.getChannelPost().getAudio()) || !isNull(update.getChannelPost().getVoice())){
                 type =MessageType.audio;
                 isFile=true;
                 body=update.getChannelPost().getCaption();

                 if(!isNull(update.getChannelPost().getAudio())){
                     fileUrl=this.getFileUrl(update.getChannelPost().getAudio().getFileId());
                 }
                 else {
                     fileUrl=this.getFileUrl(update.getChannelPost().getVoice().getFileId());
                 }

             }
             else if(!isNull(update.getChannelPost().getVideo()) || !isNull(update.getChannelPost().getVideoNote())){
                 type =MessageType.video;
                 isFile=true;
                 body=update.getChannelPost().getCaption();

                 if(!isNull(update.getMessage().getVideo())){
                     fileUrl=this.getFileUrl(update.getChannelPost().getVideo().getFileId());
                 }
                 else {
                     fileUrl=this.getFileUrl(update.getChannelPost().getVideoNote().getFileId());
                 }
             }
                
              msg.setCreatorId(creatorId)
                    .setMessageId(id)
                    .setMessage_time(time)
                    .setFileUrl(fileUrl)
                    .setIsFile(isFile)
                    .setMessageBody(body)
                    .setIsBot(isBot)
                    .setMessageType(type);

          }
            

           return msg;
        
    }

}
