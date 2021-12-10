package com.home.telegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Slf4j
@Component
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
    }

    public SendMessage handleUpdate(Update update) throws IOException {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (update.getMessage() != null && update.getMessage().hasText()){
            log.info("New message from User: {}, chatId: {}, text: {}", message.getFrom().getUserName(),
                    message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) throws IOException {
        String msgText = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (msgText){
            case "/start":
                botState = BotState.ASK_COMMANDS;
                break;
            case "/search":
                botState = BotState.SEARCH;
                break;
            case "/info":
                botState = BotState.INFO;
                break;
            case "/help":
                botState = BotState.HELP;
                break;
            default:
                botState = userDataCache.getUserCurrentState(userId);
                break;
        }
        userDataCache.setUserCurrentState(userId, botState);

        replyMessage = botStateContext.handlerInputMessage(botState, message);

        return replyMessage;
    }
}
