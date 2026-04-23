package ru.yandex.practicum.cashservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.cashservice.client.AccountResponseDto;
import ru.yandex.practicum.cashservice.client.AccountsClient;
import ru.yandex.practicum.cashservice.client.NotificationsClient;
import ru.yandex.practicum.cashservice.dto.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashServiceImplTest {

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private NotificationsClient notificationsClient;

    @InjectMocks
    private CashServiceImpl cashService;

    @Test
    void processCash_PUT_shouldIncreaseBalance() {
        AccountResponseDto accountResponse = new AccountResponseDto("user1", "Иванов Иван", BigDecimal.valueOf(1500));
        when(accountsClient.updateBalance(eq("user1"), any())).thenReturn(accountResponse);
        doNothing().when(notificationsClient).sendNotification(any());

        CashRequestDto request = new CashRequestDto(BigDecimal.valueOf(500), CashAction.PUT);
        CashResponseDto result = cashService.processCash("user1", request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500), result.getNewBalance());
        verify(accountsClient).updateBalance(eq("user1"), argThat(dto -> dto.getAmount().compareTo(BigDecimal.valueOf(500)) == 0));
    }

    @Test
    void processCash_GET_shouldDecreaseBalance() {
        AccountResponseDto accountResponse = new AccountResponseDto("user1", "Иванов Иван", BigDecimal.valueOf(500));
        when(accountsClient.updateBalance(eq("user1"), any())).thenReturn(accountResponse);
        doNothing().when(notificationsClient).sendNotification(any());

        CashRequestDto request = new CashRequestDto(BigDecimal.valueOf(500), CashAction.GET);
        CashResponseDto result = cashService.processCash("user1", request);

        verify(accountsClient).updateBalance(eq("user1"), argThat(dto -> dto.getAmount().compareTo(BigDecimal.valueOf(-500)) == 0));
    }
}
