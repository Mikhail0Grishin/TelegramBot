package com.home.telegrambot.appconfig;

import com.home.telegrambot.MyTelegramBot;
import com.home.telegrambot.botapi.TelegramFacade;
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
    private String webHookPath;
    private String botUserName;
    private String botToken;

    @Bean
    public MyTelegramBot MyTelegramBot(TelegramFacade telegramFacade) {
        MyTelegramBot myTelegramBot = new MyTelegramBot(new DefaultBotOptions(), telegramFacade);
        myTelegramBot.setBotUserName(botUserName);
        myTelegramBot.setBotToken(botToken);

        return myTelegramBot;
    }
}
