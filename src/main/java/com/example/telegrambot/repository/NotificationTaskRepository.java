package com.example.telegrambot.repository;

import com.example.telegrambot.model.NotificationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    Collection<NotificationTask>findAllBy();

    List<NotificationTask> findByTaskDate(LocalDateTime currentDate);
}
