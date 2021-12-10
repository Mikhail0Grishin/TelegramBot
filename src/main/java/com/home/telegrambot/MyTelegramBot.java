package com.home.telegrambot;

import com.home.telegrambot.botapi.TelegramFacade;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Setter
@Slf4j
public class MyTelegramBot extends TelegramWebhookBot {
    private TelegramFacade telegramFacade;

    private String botUserName;
    private String botToken;

    public MyTelegramBot(DefaultBotOptions botOptions, TelegramFacade telegramFacade){
        super(botOptions);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage replyMessageToUser = null;
        try {
            replyMessageToUser = telegramFacade.handleUpdate(update);
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        return replyMessageToUser;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
