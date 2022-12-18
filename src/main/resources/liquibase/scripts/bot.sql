-- liquibase formatted sql
-- changeset safiulina:1

CREATE TABLE Notification_task
(
    id   SERIAL,
    task TEXT,                       -- планы
    currentDate TIMESTAMP);
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE notification_task
(
    id             BIGINT NOT NULL,
    task           VARCHAR(255),
    "current_date" TIMESTAMP WITHOUT TIME ZONE,
    user_id        BIGINT,
    CONSTRAINT pk_notification_task PRIMARY KEY (id)
);

    -- changeset safiulina:2 добавление таблицы
    CREATE TABLE "User" (
        id int4 primary key,         -- id
        userName varchar (100) NOT NULL, -- имя пользователя
        registeredAt Timestamp

        );




