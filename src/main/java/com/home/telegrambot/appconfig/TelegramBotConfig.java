package com.home.telegrambot.appconfig;

import com.home.telegrambot.MyTelegramBot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class TelegramBotConfig {
    private String botUserName;
    private String botToken;
    private String apiKey;

    @Bean
    public MyTelegramBot MyTelegramBot(){
        MyTelegramBot myTelegramBot = new MyTelegramBot(new DefaultBotOptions());
        myTelegramBot.setBotUserName(botUserName);
        myTelegramBot.setBotToken(botToken);
        myTelegramBot.setApiKey(apiKey);

        return myTelegramBot;
    }
}
