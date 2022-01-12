package com.home.telegrambot.handler;

import com.home.telegrambot.botapi.BotState;
import com.home.telegrambot.cache.UserDataCache;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ChannelsSearchHandlerTest {
    @Autowired
    private ChannelsSearchHandler channelsSearchHandler;

    @Autowired
    private UserDataCache userDataCache;

    private Message message;
    private Chat chat;

    @Test
    void handleDefaultState() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Что-то пошло не так");
        sendMessage.setChatId(String.valueOf(89898989));

        chat = new Chat();
        chat.setId(89898989L);
        message = new Message();
        message.setChat(chat);

        SendMessage result = channelsSearchHandler.handle(message);

        Assert.assertEquals(sendMessage, result);
    }

    @Test
    void handleSearchState(){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Введите название канала: ");
        sendMessage.setChatId(String.valueOf(89898989));

        userDataCache.setUserCurrentState(89898989, BotState.SEARCH_OF_CHANNEL);

        chat = new Chat();
        chat.setId(89898989L);
        message = new Message();
        message.setChat(chat);

        SendMessage result = channelsSearchHandler.handle(message);

        Assert.assertEquals(sendMessage, result);
    }
}