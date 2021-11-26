package com.home.telegrambot.config;

import com.home.telegrambot.controllers.MyTelegramBot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class TelegramBotConfig {
    private String botUserName;
    private String botToken;

    @Bean
    public MyTelegramBot MyTelegramBot(){
        MyTelegramBot myTelegramBot = new MyTelegramBot(new DefaultBotOptions());
        myTelegramBot.setBotUserName(botUserName);
        myTelegramBot.setBotToken(botToken);

        return myTelegramBot;
    }
}
