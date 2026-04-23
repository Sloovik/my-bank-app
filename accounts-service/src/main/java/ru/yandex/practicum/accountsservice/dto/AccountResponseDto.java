package ru.yandex.practicum.accountsservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponseDto(
        Long id,
        String login,
        String name,
        LocalDate birthdate,
        BigDecimal balance
) {
}
