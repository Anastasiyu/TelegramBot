package com.example.telegrambot.model;


import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity(name = "Notification_task")
public class Notification_task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    final int RECONNECT_PAUSE = 10_000;

    public Notification_task(Long id, String task, LocalDateTime data, LocalDateTime time) {
        this.id = id;
        this.task = task;
        this.data = data;
        this.time = time;
    }

    private String task;
private LocalDateTime data;
private LocalDateTime time;

    public Notification_task() {

    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }




}
