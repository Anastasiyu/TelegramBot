package com.example.telegrambot.repository;

import com.example.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public abstract class UserRepository implements JpaRepository<User, Long> {

}