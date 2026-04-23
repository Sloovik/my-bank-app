package ru.yandex.practicum.cashservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cashservice.dto.CashRequestDto;
import ru.yandex.practicum.cashservice.dto.CashResponseDto;
import ru.yandex.practicum.cashservice.service.CashService;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/{login}")
    public CashResponseDto processCash(@PathVariable String login,
                                       @Valid @RequestBody CashRequestDto request) {
        return cashService.processCash(login, request);
    }
}
