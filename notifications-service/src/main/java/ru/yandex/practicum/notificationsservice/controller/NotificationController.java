package ru.yandex.practicum.notificationsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.notificationsservice.dto.NotificationDto;
import ru.yandex.practicum.notificationsservice.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendNotification(@RequestBody NotificationDto dto) {
        notificationService.sendNotification(dto);
    }
}
