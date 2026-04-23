package ru.yandex.practicum.cashservice.service;

import ru.yandex.practicum.cashservice.dto.CashRequestDto;
import ru.yandex.practicum.cashservice.dto.CashResponseDto;

public interface CashService {

    CashResponseDto processCash(String login, CashRequestDto request);
}
