package ru.yandex.practicum.accountsservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.yandex.practicum.accountsservice.dto.*;
import ru.yandex.practicum.accountsservice.exception.AccountNotFoundException;
import ru.yandex.practicum.accountsservice.exception.InsufficientFundsException;
import ru.yandex.practicum.accountsservice.model.Account;
import ru.yandex.practicum.accountsservice.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setLogin("user1");
        testAccount.setName("Иванов Иван");
        testAccount.setBirthdate(LocalDate.of(1990, 1, 1));
        testAccount.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void getAccount_shouldReturnAccountDto() {
        when(accountRepository.findByLogin("user1")).thenReturn(Optional.of(testAccount));

        AccountResponseDto result = accountService.getAccount("user1");

        assertNotNull(result);
        assertEquals("user1", result.login());
        assertEquals("Иванов Иван", result.name());
        assertEquals(BigDecimal.valueOf(1000), result.balance());
    }

    @Test
    void getAccount_shouldThrowWhenNotFound() {
        when(accountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount("unknown"));
    }

    @Test
    void updateBalance_shouldIncreaseBalance() {
        when(accountRepository.findByLogin("user1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        accountService.updateBalance("user1", BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1500), testAccount.getBalance());
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void updateBalance_shouldThrowWhenInsufficientFunds() {
        when(accountRepository.findByLogin("user1")).thenReturn(Optional.of(testAccount));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.updateBalance("user1", BigDecimal.valueOf(-2000)));
    }

    @Test
    void updateAccount_shouldUpdateNameAndBirthdate() {
        when(accountRepository.findByLogin("user1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        AccountUpdateDto updateDto = new AccountUpdateDto("Петров Петр", LocalDate.of(1985, 5, 15));
        accountService.updateAccount("user1", updateDto);

        assertEquals("Петров Петр", testAccount.getName());
        assertEquals(LocalDate.of(1985, 5, 15), testAccount.getBirthdate());
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void getOtherAccounts_shouldReturnOtherAccounts() {
        Account other = new Account();
        other.setLogin("user2");
        other.setName("Петров Петр");

        when(accountRepository.findByLoginNot("user1")).thenReturn(List.of(other));

        List<AccountShortDto> result = accountService.getOtherAccounts("user1");

        assertEquals(1, result.size());
        assertEquals("user2", result.get(0).login());
    }
}