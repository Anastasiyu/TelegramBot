package com.example.telegrambot.service;

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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PropertySource("application.properties")
@Component
public class TelegramBotService extends TelegramLongPollingBot {
  private final Logger log = LoggerFactory.getLogger(TelegramBotService.class);
  private final UserRepository userRepository;
  private final NotificationTaskRepository notificationTaskRepository;

  private final String botName;
  private final String botToken;
  static final String HELP_TEXT =
          "Этот бот создан для вывода напоминай по времени.\n\n"
                  + "Вы можете выполнять команды из главного меню слева или набрав команду:\n\n"
                  + "Введите /start чтобы увидеть приветственное сообщение\n\n"
                  + "Введите /register чтобы зарегистрироваться\n\n"
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
    listofCommands.add(new BotCommand("/help", "info how to use this bot"));
    listofCommands.add(new BotCommand("/register", "get new user"));
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


          startCommandReceived(chatId, update.getMessage().getChat().getUserName());
          break;

        case "/help":

          sendMessage(chatId, HELP_TEXT);
          break;

          case "    ":

              parseMessage(messageText);
              break;



        default:
          sendMessage(chatId, "Извините, данная команда не поддерживается!");
      }
    }
  }



  private void startCommandReceived(long chatId, String name) {

    String answer = "Привет, рад помочь Вам!";
    log.info("Ответил пользователю " + name);

    sendMessage(chatId, answer);
  }

  // метод отправить сообщение
  private void sendMessage(long chatId, String textToSend) {
    SendMessage message = new SendMessage();
    message.setChatId(String.valueOf(chatId));
    message.setText(textToSend);

    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error(ERROR_TEXT + e.getMessage());
    }
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
    List<NotificationTask> allTask = this.notificationTaskRepository.findByCurrentDate(currentDate);
    for (NotificationTask notificationTask : allTask) {
      String chat = String.valueOf(notificationTask.getUser().getChatId());
      String message = notificationTask.getTask();

    }
  }

  public Collection<User> findAllByChatId(Long chatId) {
    log.debug("Method findAllByChatId was invoked");
    return userRepository.findAllByChatId(chatId).stream()
            .filter(user -> user.getChatId() == chatId)
            .collect(Collectors.toList());
  }


  public List<NotificationTask> findByTaskDate(LocalDateTime currentDate){
    log.debug("Method findByTaskDate was invoked");
    return notificationTaskRepository.findByCurrentDate(currentDate).stream()
            .filter(notificationTask -> notificationTask.getCurrentDate() == currentDate)
            .collect(Collectors.toList());
  }
}

