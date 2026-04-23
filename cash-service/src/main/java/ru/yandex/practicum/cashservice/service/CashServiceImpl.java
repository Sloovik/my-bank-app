package ru.yandex.practicum.cashservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cashservice.client.AccountResponseDto;
import ru.yandex.practicum.cashservice.client.AccountsClient;
import ru.yandex.practicum.cashservice.client.NotificationDto;
import ru.yandex.practicum.cashservice.client.NotificationsClient;
import ru.yandex.practicum.cashservice.dto.BalanceUpdateDto;
import ru.yandex.practicum.cashservice.dto.CashAction;
import ru.yandex.practicum.cashservice.dto.CashRequestDto;
import ru.yandex.practicum.cashservice.dto.CashResponseDto;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;

    @Override
    public CashResponseDto processCash(String login, CashRequestDto request) {
        BigDecimal delta = request.getAction() == CashAction.GET
                ? request.getAmount().negate()
                : request.getAmount();

        AccountResponseDto updatedAccount = accountsClient.updateBalance(login, new BalanceUpdateDto(delta));

        String message = request.getAction() == CashAction.GET
                ? String.format("Cash withdrawal of %s completed. New balance: %s", request.getAmount(), updatedAccount.getBalance())
                : String.format("Cash deposit of %s completed. New balance: %s", request.getAmount(), updatedAccount.getBalance());

        notificationsClient.sendNotification(new NotificationDto(login, message));

        return new CashResponseDto(login, updatedAccount.getBalance(), message);
    }
}
