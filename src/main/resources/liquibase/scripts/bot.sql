-- liquibase formatted sql

-- changeset safiulina:1

CREATE TABLE bot(
    id SERIAL,
    task TEXT,
    data DATE,
    time TIME,

    -- changeset safiulina:2 добавление таблицы в схему public с данными user_table с планами пользователя
    create table user_table (
        id            int4 primary key,                -- id
        name          varchar(100) NOT NULL,           -- имя пользователя
        description   varchar(500)                     -- планы
        );

-- changeset safiulina:3 просмотр данных таблицы
select * from user_table;

)