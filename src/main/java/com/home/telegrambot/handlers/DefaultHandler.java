package com.home.telegrambot.handlers;

import com.home.telegrambot.botapi.BotState;
import com.home.telegrambot.botapi.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

@Slf4j
@Component
public class DefaultHandler implements InputMessageHandler {
    private UserDataCache userDataCache;

    public DefaultHandler(UserDataCache userDataCache){
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) throws IOException {
        SendMessage replyMessage = new SendMessage();

        StringBuilder result = new StringBuilder("Что-то пошло не так");

        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        BotState botState = userDataCache.getUserCurrentState(userId);

        if(botState.equals(BotState.HELP)){
            result = new StringBuilder("Если у вас возникли вопросы, свяжитесь с нами - mihail0910prokopenko@gmail.com");

            userDataCache.setUserCurrentState(userId, BotState.MAIN);
        }

        if(botState.equals(BotState.INFO)){
            result = new StringBuilder("Доступные команды: ");

            userDataCache.setUserCurrentState(userId, BotState.MAIN);
        }

        if (botState.equals(BotState.MAIN)){
            result = new StringBuilder("Доступные команды: \n/start\n/search\n/searchChannel\n/subscribe\n/search");
        }

        if(botState.equals(BotState.SUBSCRIBE)){
            result = new StringBuilder("Функция находится в разработке");

            userDataCache.setUserCurrentState(userId, BotState.MAIN);
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
