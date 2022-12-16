package com.example.telegrambot.Service;

import com.example.telegrambot.model.NotificationTask;
import com.example.telegrambot.model.User;
import com.example.telegrambot.repository.NotificationTaskRepository;
import com.example.telegrambot.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PropertySource("application.properties")
@Component
public class TelegramBotService extends TelegramLongPollingBot {
    private Logger log = LoggerFactory.getLogger(TelegramBotService.class);
    private final UserRepository userRepository;
    private final NotificationTaskRepository notificationTaskRepository;

    private final String botName;
    private final String botToken;
    static final String HELP_TEXT =
            "Этот бот создан для вывода напоминай по времени.\n\n"
                    + "Вы можете выполнять команды из главного меню слева или набрав команду:\n\n"
                    + "Введите /start чтобы увидеть приветственное сообщение\n\n"
                    + "Введите /memo что бы создать напоминание\n\n"
                    + "Введите /deleteMemo что бы удалить напоминание\n\n"
                    + "Введите /settings что бы сменить настройки напоминания\n\n"
                    + "Введите /help чтобы снова увидеть это сообщение";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBotService(UserRepository userRepository,
                              NotificationTaskRepository notificationTaskRepository,
                              @Value("${telegrem.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.botName = botName;
        this.botToken = botToken;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        listofCommands.add(new BotCommand("/deletedata", "delete my data"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка настройки списка команд бота: " + e.getMessage());
        }
        this.userRepository = userRepository;

    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {

        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getUserName());
                    break;

                case "/help":
                    prepareAndSendMessage(chatId, HELP_TEXT);
                    break;

                case "/register":
                    register(chatId);
                    break;

                default:
                    prepareAndSendMessage(chatId, "Извините, команда не была распознана");
            }
        }
    }

    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы действительно хотите зарегистрироваться?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("Нет");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    // регистрация пользователя
    private void registerUser(Message msg) {
        // проверяет нет ли такого пользователя
        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();
            // если нет такого пользователя добавляем
            User user = new User();

            user.setChatId(chatId);
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("пользователь сохранен: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Привет, " + name + ", приятно познакомиться с вами!" + " :blush:";
        log.info("Ответил пользователю " + name);

        sendMessage(chatId, answer);
    }

    // метод отправить сообщение
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("get random joke");

        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    // метод редактирования текста сообщения
    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    // метод принимающий сообщение
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    // метод подготовка и отправка сообщения
    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void parseMessage(String text) {
        Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String message = matcher.group(3);
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
    }

    // рассылка сообщения в заданное время
    @Scheduled(cron = "${cron.scheduler}")
    private void sendMemo() {
        LocalDateTime currentDate = LocalDateTime.now();
        List<NotificationTask> allTask = this.notificationTaskRepository.findByTaskDate(currentDate);
        for (NotificationTask notificationTask : allTask) {
            String chat = String.valueOf(notificationTask.getUser().getChatId());
            String message = notificationTask.getTask();

        }
    }
}