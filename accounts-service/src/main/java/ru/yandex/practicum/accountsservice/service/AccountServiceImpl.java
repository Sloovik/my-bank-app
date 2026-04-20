package ru.yandex.practicum.accountsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.accountsservice.client.NotificationsClient;
import ru.yandex.practicum.accountsservice.dto.*;
import ru.yandex.practicum.accountsservice.model.Account;
import ru.yandex.practicum.accountsservice.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final NotificationsClient notificationsClient;

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

        notificationsClient.sendNotification(new NotificationDto(
                login,
                "Account profile updated: name=" + dto.name()
        ));

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
            throw new IllegalStateException("Insufficient funds");
        }

        account.setBalance(newBalance);
        account = accountRepository.save(account);

        notificationsClient.sendNotification(new NotificationDto(
                login,
                "Balance updated: delta=" + delta + ", new balance=" + newBalance
        ));

        return toResponseDto(account);
    }

    private Account findByLogin(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + login));
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
