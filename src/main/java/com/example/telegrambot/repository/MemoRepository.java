package com.example.telegrambot.repository;



import com.example.telegrambot.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
 public abstract class MemoRepository implements JpaRepository<Memo, Long> {
}
