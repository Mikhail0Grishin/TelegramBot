package com.home.telegrambot;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.home.telegrambot.botapi.BotState;
import com.home.telegrambot.botapi.TelegramFacade;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Setter
@Slf4j
public class MyTelegramBot extends TelegramWebhookBot {
    private TelegramFacade telegramFacade;

    private String botUserName;
    private String botToken;
    private String webHookPath;
    private String apiKey;

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

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void setYoutubeApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey(){
        return apiKey;
    }
}
