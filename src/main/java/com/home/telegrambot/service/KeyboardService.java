package com.home.telegrambot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardService {
    public InlineKeyboardMarkup getKeyboard(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButtonInfo = new InlineKeyboardButton();
        inlineKeyboardButtonInfo.setText("Информация");
        inlineKeyboardButtonInfo.setCallbackData("/info");

        InlineKeyboardButton inlineKeyboardButtonHelp = new InlineKeyboardButton();
        inlineKeyboardButtonHelp.setText("Помощь");
        inlineKeyboardButtonHelp.setCallbackData("/help");

        InlineKeyboardButton inlineKeyboardButtonSearchVideo = new InlineKeyboardButton();
        inlineKeyboardButtonSearchVideo.setText("Поиск видео");
        inlineKeyboardButtonSearchVideo.setCallbackData("/searchVideo");

        InlineKeyboardButton inlineKeyboardButtonSearchChannel = new InlineKeyboardButton();
        inlineKeyboardButtonSearchChannel.setText("Поиск канала");
        inlineKeyboardButtonSearchChannel.setCallbackData("/searchChannel");

        InlineKeyboardButton inlineKeyboardButtonMain = new InlineKeyboardButton();
        inlineKeyboardButtonMain.setText("Меню");
        inlineKeyboardButtonMain.setCallbackData("/start");


        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButtonInfo);
        keyboardButtonsRow1.add(inlineKeyboardButtonHelp);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButtonSearchVideo);
        keyboardButtonsRow2.add(inlineKeyboardButtonSearchChannel);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButtonMain);

        keyboardButtons.add(keyboardButtonsRow3);
        keyboardButtons.add(keyboardButtonsRow2);
        keyboardButtons.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(keyboardButtons);

        return inlineKeyboardMarkup;
    }
}

