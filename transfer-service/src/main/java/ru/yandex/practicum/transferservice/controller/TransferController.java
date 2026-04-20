package ru.yandex.practicum.transferservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.transferservice.dto.TransferRequestDto;
import ru.yandex.practicum.transferservice.dto.TransferResponseDto;
import ru.yandex.practicum.transferservice.service.TransferService;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public TransferResponseDto transfer(@Valid @RequestBody TransferRequestDto request) {
        return transferService.transfer(request);
    }
}
