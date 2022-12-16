-- liquibase formatted sql

-- changeset safiulina:1

CREATE TABLE Notification_task(
    id SERIAL,
    task TEXT,           -- планы
    data DATE,
    time TIME,

    -- changeset safiulina:2 добавление таблицы
    CREATE TABLE usersDataTable (
        id            int4 primary key,                -- id
        name          varchar(100) NOT NULL,           -- имя пользователя
        registeredAt  Timestamp
        );


)