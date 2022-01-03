package com.home.telegrambot.botapi;

import com.home.telegrambot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Slf4j
@Component
public class TelegramFacade {
    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
    }

    public SendMessage handleUpdate(Update update) throws IOException {
        SendMessage replyMessage = null;

        if(update.hasCallbackQuery()){
            log.info("New CallbackQuery from User: {}, chatId: {}, text: {}",
                    update.getCallbackQuery().getFrom().getUserName(), update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getMessage().getText());
            replyMessage =  handleInputMessage(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getData());
        }

        Message message = update.getMessage();
        if (update.getMessage() != null && update.getMessage().hasText()){
            log.info("New message from User: {}, chatId: {}, text: {}", message.getFrom().getUserName(),
                    message.getChatId(), message.getText());
            replyMessage = handleInputMessage(update.getMessage(), update.getMessage().getText());
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message, String msgText) throws IOException {
        long userId = message.getChatId();

        BotState botState;
        SendMessage replyMessage;

        switch (msgText){
            case "/start":
                botState = BotState.MAIN;
                break;
            case "/searchVideo":
                botState = BotState.SEARCH_OF_VIDEO;
                break;
            case "/info":
                botState = BotState.INFO;
                break;
            case "/help":
                botState = BotState.HELP;
                break;
            case "/subscribe":
                botState = BotState.SUBSCRIBE;
                break;
            case "/searchChannel":
                botState = BotState.SEARCH_OF_CHANNEL;
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
