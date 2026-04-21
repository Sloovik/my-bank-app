package ru.yandex.practicum.transferservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.transferservice.client.AccountsClient;
import ru.yandex.practicum.transferservice.client.NotificationDto;
import ru.yandex.practicum.transferservice.client.NotificationsClient;
import ru.yandex.practicum.transferservice.dto.BalanceUpdateDto;
import ru.yandex.practicum.transferservice.dto.TransferRequestDto;
import ru.yandex.practicum.transferservice.dto.TransferResponseDto;
import ru.yandex.practicum.transferservice.exception.TransferException;
import ru.yandex.practicum.transferservice.model.TransferOperation;
import ru.yandex.practicum.transferservice.model.TransferStatus;
import ru.yandex.practicum.transferservice.repository.TransferOperationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;
    private final TransferOperationRepository operationRepository;

    @Override
    @Transactional
    public TransferResponseDto transfer(TransferRequestDto request) {
        TransferOperation operation = new TransferOperation();
        operation.setFromLogin(request.getFromLogin());
        operation.setToLogin(request.getToLogin());
        operation.setAmount(request.getAmount());
        operation.setStatus(TransferStatus.NEW);
        operation = operationRepository.save(operation);

        try {
            accountsClient.updateBalance(
                    request.getFromLogin(),
                    new BalanceUpdateDto(request.getAmount().negate())
            );
            operation.setStatus(TransferStatus.DEBIT_DONE);
            operationRepository.save(operation);
        } catch (Exception ex) {
            operation.setStatus(TransferStatus.FAILED);
            operation.setErrorMessage("Debit failed: " + ex.getMessage());
            operationRepository.save(operation);
            throw new TransferException("Не удалось списать средства: " + ex.getMessage(), ex);
        }

        try {
            accountsClient.updateBalance(
                    request.getToLogin(),
                    new BalanceUpdateDto(request.getAmount())
            );
            operation.setStatus(TransferStatus.CREDIT_DONE);
            operationRepository.save(operation);
        } catch (Exception ex) {
            log.error("Credit failed for transfer {}, compensating debit", operation.getId(), ex);
            try {
                accountsClient.updateBalance(
                        request.getFromLogin(),
                        new BalanceUpdateDto(request.getAmount())
                );
                operation.setStatus(TransferStatus.COMPENSATED);
            } catch (Exception compensationEx) {
                log.error("CRITICAL: Compensation failed for transfer {}", operation.getId(), compensationEx);
                operation.setStatus(TransferStatus.FAILED);
                operation.setErrorMessage("Compensation failed: " + compensationEx.getMessage());
            }
            operationRepository.save(operation);
            throw new TransferException("Перевод не выполнен, средства возвращены", ex);
        }

        try {
            notificationsClient.sendNotification(new NotificationDto(
                    request.getFromLogin(),
                    "Перевод %s руб. пользователю %s выполнен успешно".formatted(
                            request.getAmount(), request.getToLogin())
            ));
            notificationsClient.sendNotification(new NotificationDto(
                    request.getToLogin(),
                    "Получен перевод %s руб. от пользователя %s".formatted(
                            request.getAmount(), request.getFromLogin())
            ));
        } catch (Exception ex) {
            log.warn("Notification failed for transfer {}: {}", operation.getId(), ex.getMessage());
        }

        operation.setStatus(TransferStatus.COMPLETED);
        operationRepository.save(operation);

        return new TransferResponseDto(
                request.getFromLogin(),
                request.getToLogin(),
                request.getAmount(),
                "Перевод выполнен успешно"
        );
    }
}
