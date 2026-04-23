package ru.yandex.practicum.frontapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.frontapp.client.GatewayClient;
import ru.yandex.practicum.frontapp.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final GatewayClient gatewayClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private String getAccessToken(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName()
        );
        return client.getAccessToken().getTokenValue();
    }

    private String getCurrentLogin(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Object preferred = token.getPrincipal().getAttributes().get("preferred_username");
        if (preferred != null) {
            return preferred.toString();
        }
        return authentication.getName();
    }

    private void fillModel(Model model,
                           String name,
                           String birthdate,
                           BigDecimal sum,
                           List<AccountShortDto> accounts,
                           List<String> errors,
                           String info) {
        model.addAttribute("name", name);
        model.addAttribute("birthdate", birthdate);
        model.addAttribute("sum", sum);
        model.addAttribute("accounts", accounts != null ? accounts : List.of());
        model.addAttribute("errors", errors);
        model.addAttribute("info", info);
    }

    private void fillModelFromAccount(Model model,
                                      AccountDto account,
                                      List<AccountShortDto> accounts,
                                      List<String> errors,
                                      String info) {
        String birthdateStr = account != null && account.getBirthdate() != null
                ? account.getBirthdate().format(DateTimeFormatter.ISO_DATE)
                : null;
        fillModel(model,
                account != null ? account.getName() : null,
                birthdateStr,
                account != null ? account.getBalance() : null,
                accounts,
                errors,
                info);
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/account";
    }

    @GetMapping("/account")
    public String getAccount(Authentication auth, Model model) {
        String login = getCurrentLogin(auth);
        String token = getAccessToken(auth);

        try {
            AccountDto account = gatewayClient.getAccount(login, token);
            List<AccountShortDto> others = gatewayClient.getOtherAccounts(login, token);
            fillModelFromAccount(model, account, others, null, null);
        } catch (Exception e) {
            log.error("Error loading account for {}: {}", login, e.getMessage());
            fillModel(model, null, null, null, List.of(),
                    List.of("Ошибка загрузки данных аккаунта."), null);
        }

        return "main";
    }

    @PostMapping("/account")
    public String editAccount(Authentication auth,
                              @RequestParam("name") String name,
                              @RequestParam("birthdate") String birthdate,
                              Model model) {
        String login = getCurrentLogin(auth);
        String token = getAccessToken(auth);

        List<AccountShortDto> others = safeLoadOthers(login, token);

        LocalDate birthdateDate = null;
        List<String> errors = null;

        if (name == null || name.isBlank()) {
            errors = List.of("Поле «Фамилия Имя» обязательно для заполнения.");
        } else {
            try {
                birthdateDate = LocalDate.parse(birthdate);
            } catch (Exception e) {
                errors = List.of("Неверный формат даты рождения.");
            }
            if (errors == null && birthdateDate != null
                    && birthdateDate.isAfter(LocalDate.now().minusYears(18))) {
                errors = List.of("Возраст должен быть не менее 18 лет.");
            }
        }

        if (errors != null) {
            AccountDto current = safeLoadAccount(login, token);
            fillModelFromAccount(model, current, others, errors, null);
            return "main";
        }

        try {
            AccountDto updated = gatewayClient.updateAccount(login, name, birthdateDate, token);
            fillModelFromAccount(model, updated, others, null, "Данные аккаунта обновлены.");
        } catch (HttpClientErrorException e) {
            log.error("Error updating account {}: {}", login, e.getMessage());
            AccountDto current = safeLoadAccount(login, token);
            fillModelFromAccount(model, current, others,
                    List.of("Ошибка обновления данных: " + e.getStatusCode()), null);
        } catch (Exception e) {
            log.error("Error updating account {}: {}", login, e.getMessage());
            AccountDto current = safeLoadAccount(login, token);
            fillModelFromAccount(model, current, others,
                    List.of("Ошибка обновления данных."), null);
        }

        return "main";
    }

    @PostMapping("/cash")
    public String editCash(Authentication auth,
                           @RequestParam("value") BigDecimal value,
                           @RequestParam("action") CashAction action,
                           Model model) {
        String login = getCurrentLogin(auth);
        String token = getAccessToken(auth);

        List<AccountShortDto> others = safeLoadOthers(login, token);

        try {
            CashRequestDto req = new CashRequestDto(value, action.name());
            CashResponseDto result = gatewayClient.processCash(login, req, token);

            String info = action == CashAction.PUT
                    ? "Зачислено %s руб. Баланс: %s руб.".formatted(value, result.getNewBalance())
                    : "Снято %s руб. Баланс: %s руб.".formatted(value, result.getNewBalance());

            AccountDto updated = safeLoadAccount(login, token);
            fillModelFromAccount(model, updated, others, null, info);

        } catch (HttpClientErrorException.UnprocessableEntity | HttpClientErrorException.BadRequest e) {
            log.warn("Cash operation failed for {}: {}", login, e.getMessage());
            AccountDto current = safeLoadAccount(login, token);
            fillModelFromAccount(model, current, others,
                    List.of("Недостаточно средств на счёте."), null);
        } catch (Exception e) {
            log.error("Cash error for {}: {}", login, e.getMessage());
            String errMsg = e.getMessage() != null && e.getMessage().toLowerCase().contains("insufficient")
                    ? "Недостаточно средств на счёте."
                    : "Ошибка операции с наличными.";
            AccountDto current = safeLoadAccount(login, token);
            fillModelFromAccount(model, current, others, List.of(errMsg), null);
        }

        return "main";
    }

    @PostMapping("/transfer")
    public String transfer(Authentication auth,
                           @RequestParam("value") BigDecimal value,
                           @RequestParam("login") String toLogin,
                           Model model) {
        String currentLogin = getCurrentLogin(auth);
        String token = getAccessToken(auth);

        List<AccountShortDto> others = safeLoadOthers(currentLogin, token);

        try {
            TransferRequestDto req = new TransferRequestDto(currentLogin, toLogin, value);
            gatewayClient.processTransfer(req, token);

            AccountDto updated = safeLoadAccount(currentLogin, token);
            String toName = others.stream()
                    .filter(a -> a.getLogin().equals(toLogin))
                    .map(AccountShortDto::getName)
                    .findFirst()
                    .orElse(toLogin);
            fillModelFromAccount(model, updated, others, null,
                    "Успешно переведено %s руб. клиенту %s.".formatted(value, toName));

        } catch (HttpClientErrorException.UnprocessableEntity | HttpClientErrorException.BadRequest e) {
            log.warn("Transfer failed for {}: {}", currentLogin, e.getMessage());
            AccountDto current = safeLoadAccount(currentLogin, token);
            fillModelFromAccount(model, current, others,
                    List.of("Недостаточно средств на счёте для перевода."), null);
        } catch (Exception e) {
            log.error("Transfer error for {}: {}", currentLogin, e.getMessage());
            String errMsg = e.getMessage() != null && e.getMessage().toLowerCase().contains("insufficient")
                    ? "Недостаточно средств на счёте для перевода."
                    : "Ошибка выполнения перевода.";
            AccountDto current = safeLoadAccount(currentLogin, token);
            fillModelFromAccount(model, current, others, List.of(errMsg), null);
        }

        return "main";
    }

    private AccountDto safeLoadAccount(String login, String token) {
        try {
            return gatewayClient.getAccount(login, token);
        } catch (Exception e) {
            log.warn("Could not reload account {}: {}", login, e.getMessage());
            return null;
        }
    }

    private List<AccountShortDto> safeLoadOthers(String login, String token) {
        try {
            return gatewayClient.getOtherAccounts(login, token);
        } catch (Exception e) {
            log.warn("Could not load other accounts: {}", e.getMessage());
            return List.of();
        }
    }
}
