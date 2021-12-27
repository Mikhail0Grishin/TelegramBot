package com.home.telegrambot.botapi;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.home.telegrambot.handlers.InputMessageHandler;
import com.home.telegrambot.youtubeapi.YouTubeContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers){
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandleName(), handler));
    }

    public SendMessage handlerInputMessage(BotState botState, Message message) throws IOException {
        InputMessageHandler currentHandler = findHandler(botState);
        return currentHandler.handle(message);
    }

    private InputMessageHandler findHandler(BotState currentBotState) {
        if(isSearchOfVideo(currentBotState)){
            return messageHandlers.get(BotState.SEARCH_OF_VIDEO);
        }

        if(isSearchOfChannel(currentBotState)){
            return messageHandlers.get(BotState.SEARCH_OF_CHANNEL);
        }

        if(isDefault(currentBotState)){
            return messageHandlers.get(BotState.MAIN);
        }

        return messageHandlers.get(currentBotState);
    }

    private boolean isSearchOfVideo(BotState botState){
        switch (botState){
            case SEARCH_OF_VIDEO:
            case IN_SEARCH_OF_VIDEO:
                return true;
            default:
                return false;
        }
    }

    private boolean isSearchOfChannel(BotState botState){
        switch (botState){
            case SEARCH_OF_CHANNEL:
            case IN_SEARCH_OF_CHANNEL:
                return true;
            default:
                return false;
        }
    }

    private boolean isDefault(BotState botState){
        switch (botState){
            case HELP:
            case MAIN:
            case SUBSCRIBE:
            case IN_SUBSCRIBE:
            case INFO:
                return true;
            default:
                return false;
        }
    }
}