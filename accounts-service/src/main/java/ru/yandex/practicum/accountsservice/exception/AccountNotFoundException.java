package ru.yandex.practicum.accountsservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String login) {
        super("Аккаунт не найден: " + login);
    }
}
