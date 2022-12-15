package com.example.telegrambot.repository;

import com.example.telegrambot.model.Notification_task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract class Notification_taskRepository implements JpaRepository<Notification_task, Long> {
}
