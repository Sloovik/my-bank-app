package ru.yandex.practicum.transferservice.exception;

public class TransferException extends RuntimeException {
    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
