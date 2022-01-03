package com.home.telegrambot.cache;

import com.home.telegrambot.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UserDataCache {
    private final HashMap<Long, BotState> usersBotStates = new HashMap<>();

    public BotState getUserCurrentState(long chatId) {
        BotState botState = usersBotStates.get(chatId);
        if (botState == null){
            botState = BotState.MAIN;
        }
        return botState;
    }

    public void setUserCurrentState(long chatId, BotState botState) {
        usersBotStates.put(chatId, botState);
    }
}
