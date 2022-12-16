package com.example.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Notification_task")
public class NotificationTask {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String task;
  private LocalDateTime currentDate;

  public LocalDateTime getCurrentDate() {
    return currentDate;
  }

  public void setCurrentDate(LocalDateTime currentDate) {
    this.currentDate = currentDate;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;



  public String getTask() {
    return task;
  }

  public void setTask(String task) {
    this.task = task;
  }



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public User getUser() {return getUser();
  }
}
