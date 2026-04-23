package ru.yandex.practicum.transferservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.transferservice.config.FeignOAuth2Config;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.transferservice.dto.BalanceUpdateDto;

@FeignClient(name = "accounts-service", configuration = FeignOAuth2Config.class)
public interface AccountsClient {

    @PatchMapping("/accounts/{login}/balance")
    AccountResponseDto updateBalance(@PathVariable("login") String login,
                                     @RequestBody BalanceUpdateDto balanceUpdateDto);

    @GetMapping("/accounts/{login}")
    AccountResponseDto getAccount(@PathVariable("login") String login);
}
