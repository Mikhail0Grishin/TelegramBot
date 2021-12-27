package com.home.telegrambot.handlers;

import com.home.telegrambot.botapi.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

public interface InputMessageHandler {
    SendMessage handle(Message message) throws IOException;

    BotState getHandleName();
}
