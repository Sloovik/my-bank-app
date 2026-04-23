package ru.yandex.practicum.transferservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    private String login;
    private String name;
    private BigDecimal balance;
}
