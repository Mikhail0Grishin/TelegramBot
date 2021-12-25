package com.home.telegrambot.handlers;

import com.home.telegrambot.botapi.BotState;
import org.apache.logging.log4j.message.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface InputMessageHandler {
    SendMessage handle(Message message);

    BotState getHandleName();
}
