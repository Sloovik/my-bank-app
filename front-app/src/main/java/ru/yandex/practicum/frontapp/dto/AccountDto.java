package ru.yandex.practicum.frontapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private String login;
    private String name;
    private LocalDate birthdate;
    private BigDecimal balance;
}
