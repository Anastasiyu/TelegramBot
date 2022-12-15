package com.example.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity(name = "memoTable")
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //генерация id Spring-ом
    private Long id;

    private String memo;
}
