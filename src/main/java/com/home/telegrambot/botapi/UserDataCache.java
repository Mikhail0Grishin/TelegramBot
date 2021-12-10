package com.home.telegrambot.botapi;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UserDataCache {
    private HashMap<Long, BotState> usersBotStates = new HashMap<>();

    public BotState getUserCurrentState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null){
            botState = BotState.ASK_COMMANDS;
        }
        return botState;
    }

    public void setUserCurrentState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }
}
