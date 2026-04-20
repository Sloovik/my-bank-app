package ru.yandex.practicum.accountsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.accountsservice.dto.*;
import ru.yandex.practicum.accountsservice.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{login}")
    public AccountResponseDto getAccount(@PathVariable String login) {
        return accountService.getAccount(login);
    }

    @PutMapping("/{login}")
    public AccountResponseDto updateAccount(
            @PathVariable String login,
            @Valid @RequestBody AccountUpdateDto dto) {
        return accountService.updateAccount(login, dto);
    }

    @GetMapping
    public List<AccountShortDto> getOtherAccounts(
            @RequestParam String excludeLogin) {
        return accountService.getOtherAccounts(excludeLogin);
    }

    @PatchMapping("/{login}/balance")
    public AccountResponseDto updateBalance(
            @PathVariable String login,
            @RequestBody BalanceUpdateDto dto) {
        return accountService.updateBalance(login, dto.amount());
    }
}
