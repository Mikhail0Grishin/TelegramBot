package com.home.telegrambot.handler;

import com.home.telegrambot.botapi.BotState;
import com.home.telegrambot.cache.UserDataCache;
import com.home.telegrambot.service.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class DefaultHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final KeyboardService keyboardService;

    public DefaultHandler(UserDataCache userDataCache, KeyboardService keyboardService){
        this.keyboardService = keyboardService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        SendMessage replyMessage = new SendMessage();

        StringBuilder result = new StringBuilder("Что-то пошло не так");

        long chatId = message.getChatId();

        BotState botState = userDataCache.getUserCurrentState(chatId);

        if(botState.equals(BotState.HELP)){
            result = new StringBuilder("Если у вас возникли вопросы, свяжитесь с нами - mihail0910prokopenko@gmail.com");

            userDataCache.setUserCurrentState(chatId, BotState.MAIN);
        }

        if(botState.equals(BotState.INFO)){
            result = new StringBuilder("Этот бот создан для работы с ютуб апи.");

            userDataCache.setUserCurrentState(chatId, BotState.MAIN);
        }

        if (botState.equals(BotState.MAIN)){
            result = new StringBuilder("Доступные команды:");
            replyMessage.setReplyMarkup(keyboardService.getKeyboard());
        }

        if(botState.equals(BotState.SUBSCRIBE)){
            result = new StringBuilder("Функция находится в разработке");

            userDataCache.setUserCurrentState(chatId, BotState.MAIN);
        }

        replyMessage.enableHtml(true);
        replyMessage.setParseMode(ParseMode.HTML);
        replyMessage.setChatId(String.valueOf(chatId));
        replyMessage.setText(result.toString());

        return replyMessage;
    }

    @Override
    public BotState getHandleName() {
        return BotState.MAIN;
    }
}
