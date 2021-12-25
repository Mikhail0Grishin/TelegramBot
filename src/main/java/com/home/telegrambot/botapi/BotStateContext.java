package com.home.telegrambot.botapi;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.home.telegrambot.handlers.InputMessageHandler;
import com.home.telegrambot.youtubeapi.YouTubeContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private UserDataCache userDataCache;
    private YouTubeContext youTubeContext;

    public BotStateContext(UserDataCache userDataCache, YouTubeContext youTubeContext, List<InputMessageHandler> messageHandlers){
        this.youTubeContext = youTubeContext;
        this.userDataCache = userDataCache;
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandleName(), handler));
    }

    public SendMessage handlerInputMessage(BotState botState, Message message) throws IOException {
        SendMessage replyMessage = new SendMessage();
        StringBuilder result = new StringBuilder("Что-то пошло не так");

        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

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

            userDataCache.setUserCurrentState(userId, BotState.MAIN);
        }

        if(botState.equals(BotState.IN_SEARCH_OF_CHANNEL)){
            String keyWords = message.getText();
            SearchListResponse response = youTubeContext.searchForChannel(keyWords);

            List<SearchResult> results = response.getItems();
            Iterator<SearchResult> resultIterator = results.iterator();
            result = new StringBuilder();
            log.info(results.toString());
            while(resultIterator.hasNext()){
                SearchResult singleChannel = resultIterator.next();
                ResourceId rId = singleChannel.getId();
                SearchResultSnippet snippet = singleChannel.getSnippet();

                if(rId.getKind().equals("youtube#channel")) {
                    result.append("URL: " + "https://www.youtube.com/channel/")
                            .append(rId.getChannelId())
                            .append("\n")
                            .append("Title: ")
                            .append(snippet.getTitle())
                            .append("\n");
                }
            }

            userDataCache.setUserCurrentState(userId, BotState.MAIN);
        }

        if(botState.equals(BotState.SEARCH_OF_VIDEO)){
            result = new StringBuilder("Введите ключевые слова: ");

            userDataCache.setUserCurrentState(userId, BotState.IN_SEARCH_OF_VIDEO);
        }

        if(botState.equals(BotState.SEARCH_OF_CHANNEL)){
            result = new StringBuilder("Введите название канала: ");

            userDataCache.setUserCurrentState(userId, BotState.IN_SEARCH_OF_CHANNEL);
        }

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
}