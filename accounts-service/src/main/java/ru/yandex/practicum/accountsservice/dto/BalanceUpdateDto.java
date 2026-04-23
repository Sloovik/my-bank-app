package ru.yandex.practicum.accountsservice.dto;

import java.math.BigDecimal;

public record BalanceUpdateDto(
        BigDecimal amount
) {
}
