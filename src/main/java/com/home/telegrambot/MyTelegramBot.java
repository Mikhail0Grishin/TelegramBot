package com.home.telegrambot;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.Setter;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Setter
public class MyTelegramBot extends TelegramWebhookBot {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // max value for single page is 50
    private static final short NUMBER_OF_VIDEOS_RETURNED = 5;
    private BotState botState;

    private static YouTube youTube;
    private static String apiYoutube;

    private final String INFO_LABEL = "Помощь";
    private final String SEARCH_LABEL = "Поиск";
    private final String SUBSCRIBE_LABEL = "Подписаться на канал";

    private String botUserName;
    private String botToken;
    private String webHookPath;

    public MyTelegramBot(DefaultBotOptions botOptions){
        super(botOptions);
        botState = BotState.ASK_COMMANDS;
    }

    public void setApiKey(String apiKey) {
        apiYoutube = apiKey;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if(update.getMessage() != null && update.getMessage().hasText()){
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            try {
                SendMessage message = getCommandResponse(text, update.getMessage().getFrom(), chatId, botState);
                message.enableHtml(true);
                message.setParseMode(ParseMode.HTML);
                message.setChatId(chatId);
                execute(message);
            } catch (TelegramApiException | IOException e) {
                String exceptionMessage = e.getMessage();
                SendMessage message = handleNotFoundCommand(exceptionMessage);
                message.setChatId(chatId);
            }
        } else if (update.hasCallbackQuery()){
            try {
                SendMessage message = getCommandResponse(update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom(), String.valueOf(update.getCallbackQuery().getMessage().getChatId()), botState);
                message.enableHtml(true);
                message.setParseMode(ParseMode.HTML);
                message.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                execute(message);
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    private void setBotState(BotState botState){
        this.botState = botState;
    }

    private enum COMMANDS {
        INFO("/info"),
        START("/start"),
        SEARCH("/search"),
        SUBSCRIBE("/subscribe");

        private final String command;

        COMMANDS(String command){
            this.command = command;
        }

        public String getCommand(){
            return command;
        }
    }

    public String getWebHookPath(){
        return webHookPath;
    }
    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private SendMessage handleNotFoundCommand(String exceptionMessage) {
        SendMessage message = new SendMessage();
        message.setText(exceptionMessage);
        message.setReplyMarkup(getKeyboard());

        return message;
    }

    private InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(INFO_LABEL);
        inlineKeyboardButton.setCallbackData(COMMANDS.INFO.getCommand());

        InlineKeyboardButton inlineKeyboardButtonSearch = new InlineKeyboardButton();
        inlineKeyboardButtonSearch.setText(SEARCH_LABEL);
        inlineKeyboardButtonSearch.setCallbackData(COMMANDS.SEARCH.getCommand());

        InlineKeyboardButton inlineKeyboardButtonSubscribe = new InlineKeyboardButton();
        inlineKeyboardButtonSubscribe.setText(SUBSCRIBE_LABEL);
        inlineKeyboardButtonSubscribe.setCallbackData(COMMANDS.SUBSCRIBE.getCommand());

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

    private SendMessage getCommandResponse(String text, User user, String chatId, BotState botState) throws IOException {
        if (text.equals(COMMANDS.INFO.getCommand())){
            setBotState(BotState.INFO);
            return handleInfoCommand();
        }

        if (text.equals(COMMANDS.START.getCommand())){
            setBotState(BotState.ASK_COMMANDS);
            return handleStartCommand();
        }

        if (text.equals(COMMANDS.SEARCH.getCommand()) || botState.equals(BotState.ASK_KEYWORDS)){
            return handleSearchCommand(text, user, chatId, botState);
        }

        if (text.equals(COMMANDS.SUBSCRIBE.getCommand())){
            return handleSubscribeCommand();
        }

        return handleNotFoundCommand("Что то пошло не так");
    }

    private SendMessage handleSubscribeCommand() {
        SendMessage message = new SendMessage();
        message.setText("Подписаться на канал");
        message.setReplyMarkup(getKeyboard());

        return message;
    }

    private SendMessage handleSearchCommand(String textFromMessage, User user, String chatId, BotState botState) throws IOException {
        SendMessage message = new SendMessage();
        String result = "Введите ключевые слова: ";
        if (botState.equals(BotState.ASK_KEYWORDS)){
            String keyWords = textFromMessage;
            SearchListResponse response = search(keyWords);
            List<SearchResult> results = response.getItems();
            Iterator<SearchResult> resultIterator = results.iterator();
            result = "";

            System.out.println(response);
            while(resultIterator.hasNext()){
                SearchResult singleVideo = resultIterator.next();
                ResourceId rId = singleVideo.getId();

                if (rId.getKind().equals("youtube#video")) {
                    result += "URL: " + "https://www.youtube.com/watch?v=" + rId.getVideoId() + "\n" + "Title: " + singleVideo.getSnippet().getTitle() + "\n";
                }
            }
            setBotState(BotState.ASK_COMMANDS);
        }

        message.setText(result);
        setBotState(BotState.ASK_KEYWORDS);

        return message;
    }

    private SendMessage handleStartCommand() {
        SendMessage message = new SendMessage();
        message.setText("Доступные команды: ");
        message.setReplyMarkup(getKeyboard());
        return message;
    }

    private SendMessage handleInfoCommand() {
        SendMessage message = new SendMessage();
        message.setText("Это канал для работы с youtube Api");
        message.setReplyMarkup(getKeyboard());

        return message;
    }

    private SearchListResponse search(String term) throws IOException {
        SearchListResponse searchListResponse = null;

        try {
            youTube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {}).setApplicationName("youtube-cmdline-search-sample").build();

            YouTube.Search.List search = youTube.search().list("id,snippet");

            // TODO: accept term from users
            String queryTerm = term;

            search.setKey(apiYoutube);
            search.setQ(queryTerm);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url),nextPageToken,pageInfo,prevPageToken");
            search.setMaxResults((long) NUMBER_OF_VIDEOS_RETURNED);

            searchListResponse = search.execute();
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
        }

        return searchListResponse;
    }
}
