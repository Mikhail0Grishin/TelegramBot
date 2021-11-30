package com.home.telegrambot;

import lombok.Setter;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Setter
public class MyTelegramBot extends TelegramLongPollingBot {
    private final String INFO_LABEL = "Помощь";
    private final String SEARCH_LABEL = "Поиск";
    private final String SUBSCRIBE_LABEL = "Подписаться на канал";

    private String botUserName;
    private String botToken;

    public MyTelegramBot(DefaultBotOptions botOptions){
        super(botOptions);
    }

    private enum COMMANDS {
        INFO("/info"),
        START("/start"),
        SEARCH("/search"),
        SUBSCRIBE("/subscribe");

        private String command;

        COMMANDS(String command){
            this.command = command;
        }

        public String getCommand(){
            return command;
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.getMessage() != null && update.getMessage().hasText()){
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            try {
                SendMessage message = getCommandResponse(text, update.getMessage().getFrom(), chatId);
                message.enableHtml(true);
                message.setParseMode(ParseMode.HTML);
                message.setChatId(chatId);
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                SendMessage message = handleNotFoundCommand();
                message.setChatId(chatId);
            }
        } else if (update.hasCallbackQuery()){
            try {
                SendMessage message = getCommandResponse(update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom(), String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                message.enableHtml(true);
                message.setParseMode(ParseMode.HTML);
                message.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private SendMessage handleNotFoundCommand() {
        SendMessage message = new SendMessage();
        message.setText("Что-то пошло не так, выберите команду: ");
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

    private SendMessage getCommandResponse(String text, User from, String valueOf) {
        if (text.equals(COMMANDS.INFO.getCommand())){
            return handleInfoCommand();
        }

        if (text.equals(COMMANDS.START.getCommand())){
            return handleStartCommand();
        }

        if (text.equals(COMMANDS.SEARCH.getCommand())){
            return handleSearchCommand();
        }

        if (text.equals(COMMANDS.SUBSCRIBE.getCommand())){
            return handleSubscribeCommand();
        }

        return handleNotFoundCommand();
    }

    private SendMessage handleSubscribeCommand() {
        SendMessage message = new SendMessage();
        message.setText("Подписаться на канал");
        message.setReplyMarkup(getKeyboard());

        return message;
    }

    private SendMessage handleSearchCommand() {
        SendMessage message = new SendMessage();
        message.setText("Поиск видео");
        message.setReplyMarkup(getKeyboard());

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
}
