package com.home.telegrambot.handler;

import com.home.telegrambot.botapi.BotState;
import com.home.telegrambot.cache.UserDataCache;
import com.home.telegrambot.service.KeyboardService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class DefaultHandlerTest {
    @Autowired
    private DefaultHandler defaultHandler;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private UserDataCache userDataCache;

    private Message message;
    private Chat chat;

    @Test
    void handleMainState() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Доступные команды:");
        sendMessage.setChatId(String.valueOf(89898989));
        sendMessage.setReplyMarkup(keyboardService.getKeyboard());

        chat = new Chat();
        chat.setId(89898989L);
        message = new Message();
        message.setChat(chat);

        userDataCache.setUserCurrentState(89898989, BotState.MAIN);

        SendMessage result = defaultHandler.handle(message);

        Assert.assertEquals(sendMessage, result);
    }

    @Test
    void handleHelpState() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Если у вас возникли вопросы, свяжитесь с нами - mihail0910prokopenko@gmail.com");
        sendMessage.setChatId(String.valueOf(89898981));

        chat = new Chat();
        chat.setId(89898981L);
        message = new Message();
        message.setChat(chat);

        userDataCache.setUserCurrentState(89898981, BotState.HELP);

        SendMessage result = defaultHandler.handle(message);

        Assert.assertEquals(sendMessage, result);
    }

    @Test
    void handleInfoState() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Этот бот создан для работы с ютуб апи.");
        sendMessage.setChatId(String.valueOf(89898919));

        chat = new Chat();
        chat.setId(89898919L);
        message = new Message();
        message.setChat(chat);

        userDataCache.setUserCurrentState(89898919, BotState.INFO);

        SendMessage result = defaultHandler.handle(message);

        Assert.assertEquals(sendMessage, result);
    }

    @Test
    void handleSubscribeState() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Функция находится в разработке");
        sendMessage.setChatId(String.valueOf(89898189));

        chat = new Chat();
        chat.setId(89898189L);
        message = new Message();
        message.setChat(chat);

        userDataCache.setUserCurrentState(89898189, BotState.SUBSCRIBE);

        SendMessage result = defaultHandler.handle(message);

        Assert.assertEquals(sendMessage, result);
    }
}