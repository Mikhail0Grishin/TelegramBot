package com.home.telegrambot.botapi;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.home.telegrambot.youtubeapi.YouTubeContext;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Setter
@Component
public class BotStateContext {
    private UserDataCache userDataCache;
    private YouTubeContext youTubeContext;

    public BotStateContext(UserDataCache userDataCache, YouTubeContext youTubeContext){
        this.youTubeContext = youTubeContext;
        this.userDataCache = userDataCache;
    }

    private final String INFO_LABEL = "Помощь";
    private final String SEARCH_LABEL = "Поиск";
    private final String SUBSCRIBE_LABEL = "Подписаться на канал";

    public SendMessage handlerInputMessage(BotState botState, Message message) throws IOException {
        SendMessage replyMessage = new SendMessage();
        StringBuilder result = new StringBuilder("Что-то пошло не так");

        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        if (botState.equals(BotState.ASK_KEYWORDS)){
            String keyWords = message.getText();
            SearchListResponse response = youTubeContext.search(keyWords);
            List<SearchResult> results = response.getItems();
            Iterator<SearchResult> resultIterator = results.iterator();
            result = new StringBuilder();
            System.out.println(response);
            while(resultIterator.hasNext()){
                SearchResult singleVideo = resultIterator.next();
                ResourceId rId = singleVideo.getId();
                if (rId.getKind().equals("youtube#video")) {
                    result.append("URL: " + "https://www.youtube.com/watch?v=").append(rId.getVideoId()).append("\n").append("Title: ").append(singleVideo.getSnippet().getTitle()).append("\n");
                }
            }

            userDataCache.setUserCurrentState(userId, BotState.ASK_COMMANDS);
        }

        if(botState.equals(BotState.SEARCH)){
            result = new StringBuilder("Введите ключевые слова: ");

            userDataCache.setUserCurrentState(userId, BotState.ASK_KEYWORDS);
        }

        if(botState.equals(BotState.HELP)){
            result = new StringBuilder("Если у вас возникли вопросы, свяжитесь с нами - mihail0910prokopenko@gmail.com");

            userDataCache.setUserCurrentState(userId, BotState.ASK_COMMANDS);
        }

        if(botState.equals(BotState.INFO)){
            result = new StringBuilder("Доступные команды: ");
            replyMessage.setReplyMarkup(getKeyboard());

            userDataCache.setUserCurrentState(userId, BotState.ASK_COMMANDS);
            replyMessage.setReplyMarkup(getKeyboard());
        }

        if (botState.equals(BotState.ASK_COMMANDS)){
            result = new StringBuilder("Доступные команды: ");
            replyMessage.setReplyMarkup(getKeyboard());
        }

        if(botState.equals(BotState.SUBSCRIBE)){
            result = new StringBuilder("Функция находится в разработке");

            userDataCache.setUserCurrentState(userId, BotState.ASK_COMMANDS);
            replyMessage.setReplyMarkup(getKeyboard());
        }

        replyMessage.enableHtml(true);
        replyMessage.setParseMode(ParseMode.HTML);
        replyMessage.setChatId(String.valueOf(chatId));
        replyMessage.setText(result.toString());

        return replyMessage;
    }

    private InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(INFO_LABEL);
        inlineKeyboardButton.setCallbackData("/info");

        InlineKeyboardButton inlineKeyboardButtonSearch = new InlineKeyboardButton();
        inlineKeyboardButtonSearch.setText(SEARCH_LABEL);
        inlineKeyboardButtonSearch.setCallbackData("/search");

        InlineKeyboardButton inlineKeyboardButtonSubscribe = new InlineKeyboardButton();
        inlineKeyboardButtonSubscribe.setText(SUBSCRIBE_LABEL);
        inlineKeyboardButtonSubscribe.setCallbackData("/subscribe");

        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButtonSearch);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButtonSubscribe);

        keyboardButtons.add(keyboardButtonsRow1);
        keyboardButtons.add(keyboardButtonsRow2);
        keyboardButtons.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(keyboardButtons);

        return inlineKeyboardMarkup;
    }
}