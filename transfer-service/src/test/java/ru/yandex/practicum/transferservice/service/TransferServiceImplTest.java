package ru.yandex.practicum.transferservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.yandex.practicum.transferservice.client.AccountResponseDto;
import ru.yandex.practicum.transferservice.client.AccountsClient;
import ru.yandex.practicum.transferservice.dto.*;
import ru.yandex.practicum.transferservice.metrics.TransferMetrics;
import ru.yandex.practicum.transferservice.repository.TransferOperationRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TransferOperationRepository operationRepository;

    @Mock
    private TransferMetrics transferMetrics;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    void transfer_shouldDebitSenderAndCreditReceiver() {
        when(operationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccountResponseDto senderAfter = new AccountResponseDto("user1", "Иванов Иван", BigDecimal.valueOf(500));
        AccountResponseDto receiverAfter = new AccountResponseDto("user2", "Петров Петр", BigDecimal.valueOf(1600));

        when(accountsClient.updateBalance(eq("user1"), argThat(dto -> dto.getAmount().signum() < 0)))
                .thenReturn(senderAfter);
        when(accountsClient.updateBalance(eq("user2"), argThat(dto -> dto.getAmount().signum() > 0)))
                .thenReturn(receiverAfter);

        TransferRequestDto request = new TransferRequestDto("user1", "user2", BigDecimal.valueOf(500));
        TransferResponseDto result = transferService.transfer(request);

        assertNotNull(result);
        assertEquals("user1", result.getFromLogin());
        assertEquals("user2", result.getToLogin());
        assertEquals(BigDecimal.valueOf(500), result.getAmount());

        verify(accountsClient, times(2)).updateBalance(anyString(), any());
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
    }
}