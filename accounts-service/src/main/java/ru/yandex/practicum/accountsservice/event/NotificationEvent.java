package ru.yandex.practicum.accountsservice.event;

public record NotificationEvent(String login, String message) {}