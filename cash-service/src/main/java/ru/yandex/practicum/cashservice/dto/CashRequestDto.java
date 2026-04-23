package ru.yandex.practicum.cashservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashRequestDto {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private CashAction action;
}
