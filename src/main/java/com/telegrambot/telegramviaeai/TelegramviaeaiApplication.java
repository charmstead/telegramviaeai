package com.telegrambot.telegramviaeai;

import com.telegrambot.telegramviaeai.LibaryConfig.TelegramViaeaiBot;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@SpringBootApplication
public class TelegramviaeaiApplication {

    private static final Logger log =LoggerFactory.getLogger(TelegramviaeaiApplication.class);
    
    @Inject
    Environment env;
    
    @Autowired
    private TelegramViaeaiBot telegramViaeaiBot;
     
    
    private static TelegramViaeaiBot viaeaiBot;
    
    @Autowired
    public TelegramviaeaiApplication(TelegramViaeaiBot telegramViaeaiBot){
        viaeaiBot=telegramViaeaiBot;
    }
            
            
	public static void main(String[] args) {
		SpringApplication.run(TelegramviaeaiApplication.class, args);
                
                              
                ApiContextInitializer.init();
                TelegramBotsApi botsApi = new TelegramBotsApi();
                              
//                TelegramViaeaiBot telegramViaeaiBot =new TelegramViaeaiBot();
                log.info("Inside the static context");
                log.info(String.format("Token:%s\nUsername:%s\n", viaeaiBot.getBotToken(),viaeaiBot.getBotUsername()));
                
                try {
                   botsApi.registerBot(viaeaiBot);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
	}
       
    @PostConstruct
    public void init() {
        log.info(String.format("Token:%s\nUsername:%s\n", telegramViaeaiBot.getBotToken(),telegramViaeaiBot.getBotUsername()));
    }

}
