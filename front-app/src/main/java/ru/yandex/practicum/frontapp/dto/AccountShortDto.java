package ru.yandex.practicum.frontapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountShortDto {

    private String login;
    private String name;
}
