package ru.yandex.practicum.transferservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.transferservice.client.AccountsClient;
import ru.yandex.practicum.transferservice.client.NotificationDto;
import ru.yandex.practicum.transferservice.client.NotificationsClient;
import ru.yandex.practicum.transferservice.dto.BalanceUpdateDto;
import ru.yandex.practicum.transferservice.dto.TransferRequestDto;
import ru.yandex.practicum.transferservice.dto.TransferResponseDto;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;

    @Override
    public TransferResponseDto transfer(TransferRequestDto request) {
        accountsClient.updateBalance(
                request.getFromLogin(),
                new BalanceUpdateDto(request.getAmount().negate())
        );

        accountsClient.updateBalance(
                request.getToLogin(),
                new BalanceUpdateDto(request.getAmount())
        );

        String senderMessage = String.format(
                "Transfer of %s to %s completed successfully.",
                request.getAmount(), request.getToLogin()
        );
        notificationsClient.sendNotification(new NotificationDto(request.getFromLogin(), senderMessage));

        String recipientMessage = String.format(
                "You received a transfer of %s from %s.",
                request.getAmount(), request.getFromLogin()
        );
        notificationsClient.sendNotification(new NotificationDto(request.getToLogin(), recipientMessage));

        return new TransferResponseDto(
                request.getFromLogin(),
                request.getToLogin(),
                request.getAmount(),
                String.format("Transfer of %s from %s to %s completed successfully.",
                        request.getAmount(), request.getFromLogin(), request.getToLogin())
        );
    }
}
