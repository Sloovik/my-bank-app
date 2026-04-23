package ru.yandex.practicum.transferservice.service;

import ru.yandex.practicum.transferservice.dto.TransferRequestDto;
import ru.yandex.practicum.transferservice.dto.TransferResponseDto;

public interface TransferService {

    TransferResponseDto transfer(TransferRequestDto request);
}
