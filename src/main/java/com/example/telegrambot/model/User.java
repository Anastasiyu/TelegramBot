package com.example.telegrambot.model;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Collection;
@Entity(name = "user")
public class User {

    @Id
    private Long chatId;


    private String userName;

    private Timestamp registeredAt;
    @OneToMany(mappedBy = "user")
    private Collection<NotificationTask> notification_tasks;


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "Пользователь" +
                "  " + chatId +
                "/ " + userName +
                ", " + registeredAt;
    }
}
