package com.example.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("application.properties")
@Configuration
public class TelegramBotConfiguration {
    @Value("${telegrem.bot.name}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String token;





    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }


}
