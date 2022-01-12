package com.home.telegrambot.handler;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.home.telegrambot.botapi.BotState;
import com.home.telegrambot.cache.UserDataCache;
import com.home.telegrambot.youtubeapi.YouTubeContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Setter
@Slf4j
@Component
public class VideosSearchHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private YouTubeContext youTubeContext;

    public VideosSearchHandler(UserDataCache userDataCache, YouTubeContext youTubeContext){
        this.userDataCache = userDataCache;
        this.youTubeContext = youTubeContext;
    }

    @Override
    public SendMessage handle(Message message) throws IOException {
        SendMessage replyMessage = new SendMessage();
        StringBuilder result = new StringBuilder("Что-то пошло не так");

        long chatId = message.getChatId();

        BotState botState = userDataCache.getUserCurrentState(chatId);

        if (botState.equals(BotState.IN_SEARCH_OF_VIDEO)){
            String keyWords = message.getText();

            SearchListResponse response = youTubeContext.searchForVideo(keyWords);

            List<SearchResult> results = response.getItems();
            Iterator<SearchResult> resultIterator = results.iterator();
            result = new StringBuilder();

            log.info(results.toString());

            while(resultIterator.hasNext()){
                SearchResult singleVideo = resultIterator.next();
                ResourceId rId = singleVideo.getId();

                if (rId.getKind().equals("youtube#video")) {
                    result.append("URL: " + "https://www.youtube.com/watch?v=")
                            .append(rId.getVideoId()).append("\n")
                            .append("Title: ")
                            .append(singleVideo.getSnippet().getTitle())
                            .append("\n");
                }
            }

            userDataCache.setUserCurrentState(chatId, BotState.MAIN);
        }

        if(botState.equals(BotState.SEARCH_OF_VIDEO)){
            result = new StringBuilder("Введите ключевые слова: ");

            userDataCache.setUserCurrentState(chatId, BotState.IN_SEARCH_OF_VIDEO);
        }

        replyMessage.setChatId(String.valueOf(chatId));
        replyMessage.setText(result.toString());

        return replyMessage;
    }

    @Override
    public BotState getHandleName() {
        return BotState.SEARCH_OF_VIDEO;
    }
}
