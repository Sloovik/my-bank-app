package ru.yandex.practicum.accountsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.accountsservice.dto.*;
import ru.yandex.practicum.accountsservice.event.NotificationEvent;
import ru.yandex.practicum.accountsservice.exception.AccountNotFoundException;
import ru.yandex.practicum.accountsservice.exception.InsufficientFundsException;
import ru.yandex.practicum.accountsservice.metrics.AccountMetrics;
import ru.yandex.practicum.accountsservice.model.Account;
import ru.yandex.practicum.accountsservice.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountMetrics accountMetrics;

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto getAccount(String login) {
        Account account = findByLogin(login);
        return toResponseDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDto updateAccount(String login, AccountUpdateDto dto) {
        validateAge(dto.birthdate());

        Account account = findByLogin(login);
        account.setName(dto.name());
        account.setBirthdate(dto.birthdate());
        account = accountRepository.save(account);

        eventPublisher.publishEvent(new NotificationEvent(login, "Данные аккаунта обновлены"));

        return toResponseDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountShortDto> getOtherAccounts(String currentLogin) {
        return accountRepository.findByLoginNot(currentLogin)
                .stream()
                .map(a -> new AccountShortDto(a.getLogin(), a.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountResponseDto updateBalance(String login, BigDecimal delta) {
        Account account = findByLogin(login);
        BigDecimal newBalance = account.getBalance().add(delta);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Insufficient funds for login={}: requested={}, balance={}", login, delta.abs(), account.getBalance());
            accountMetrics.incrementFailedWithdrawal(login);
            throw new InsufficientFundsException(login, delta.abs(), account.getBalance());
        }

        account.setBalance(newBalance);
        account = accountRepository.save(account);

        String msg = delta.compareTo(BigDecimal.ZERO) > 0
                ? "Баланс пополнен на " + delta + " руб. Текущий баланс: " + newBalance + " руб."
                : "Списано " + delta.abs() + " руб. Текущий баланс: " + newBalance + " руб.";

        eventPublisher.publishEvent(new NotificationEvent(login, msg));

        return toResponseDto(account);
    }

    private Account findByLogin(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new AccountNotFoundException(login));
    }

    private void validateAge(LocalDate birthdate) {
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("Account holder must be at least 18 years old");
        }
    }

    private AccountResponseDto toResponseDto(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getLogin(),
                account.getName(),
                account.getBirthdate(),
                account.getBalance()
        );
    }
}