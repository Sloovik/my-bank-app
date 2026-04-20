package ru.yandex.practicum.frontapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    private String fromLogin;
    private String toLogin;
    private BigDecimal amount;
}
