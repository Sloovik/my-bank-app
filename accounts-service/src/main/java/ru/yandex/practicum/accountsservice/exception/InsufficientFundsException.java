package ru.yandex.practicum.accountsservice.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String login, BigDecimal requested, BigDecimal available) {
        super("Недостаточно средств на счёте %s: запрошено %s, доступно %s"
                .formatted(login, requested, available));
    }
}
