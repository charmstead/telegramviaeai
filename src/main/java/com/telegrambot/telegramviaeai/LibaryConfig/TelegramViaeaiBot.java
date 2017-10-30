/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telegrambot.telegramviaeai.LibaryConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.telegrambot.telegramviaeai.MessageMapper.TelegramMessageMapper;
import java.util.logging.Level;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 * @author tomide
 */
//see https://github.com/rubenlagus/TelegramBots/wiki/Getting-Started
@Component
public class TelegramViaeaiBot extends TelegramLongPollingBot{

    private static final Logger log = LoggerFactory.getLogger(TelegramViaeaiBot.class);
    
    @Inject
    private Environment env;
    
    @Value("${ACCESSTOKEN}")
    private String token;

    @Value("${BOT_USERNAME}")
    private String username;
    
    
 
    @Autowired
    private TelegramMessageMapper telegramMessagerMapper;
    

    @Override
    public String getBotUsername() {
        // TODO
        
        return this.username;
    }

    @Override
    public String getBotToken() {
        // TODO
        return this.token;
    }

    @Override
    public synchronized void onUpdateReceived(Update update) {
        
        ObjectMapper mapper= new ObjectMapper();
        
        
        if (update.hasMessage() && update.getMessage().hasText()) {
           String message_text = update.getMessage().getText();
           long chat_id = update.getMessage().getChatId();

           Message updateMsg = update.getMessage();
           
            try {
                log.info(String.format("INCOMING TELEGRAM MESSAGE\n%s", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updateMsg)));
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(TelegramViaeaiBot.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            com.telegrambot.telegramviaeai.viaeaibotMessage.Message msg =telegramMessagerMapper.maptoViaeaiMessage(update);
           
             try {
                log.info(String.format("MAPS TO MESSAGE\n%s", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msg)));
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(TelegramViaeaiBot.class.getName()).log(Level.SEVERE, null, ex);
            }
             
               SendMessage message = new SendMessage() // Create a message object object
                       .setChatId(chat_id)
                       .setText(message_text);
               
               
               try {
                   
                   execute(message); // Sending our message object to user
               } catch (TelegramApiException e) {
                   e.printStackTrace();
               }

       } 

    }

//    @Override
//    public void onUpdatesReceived(List<Update> list) {
//        super.onUpdatesReceived(list); //To change body of generated methods, choose Tools | Templates.
//    }
    
}
