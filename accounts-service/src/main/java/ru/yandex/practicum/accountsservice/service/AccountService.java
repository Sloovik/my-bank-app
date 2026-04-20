package ru.yandex.practicum.accountsservice.service;

import ru.yandex.practicum.accountsservice.dto.AccountResponseDto;
import ru.yandex.practicum.accountsservice.dto.AccountShortDto;
import ru.yandex.practicum.accountsservice.dto.AccountUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountResponseDto getAccount(String login);

    AccountResponseDto updateAccount(String login, AccountUpdateDto dto);

    List<AccountShortDto> getOtherAccounts(String currentLogin);

    AccountResponseDto updateBalance(String login, BigDecimal delta);
}
