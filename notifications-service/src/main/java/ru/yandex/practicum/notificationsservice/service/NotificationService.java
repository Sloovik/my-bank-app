package ru.yandex.practicum.notificationsservice.service;

import ru.yandex.practicum.notificationsservice.dto.NotificationDto;

public interface NotificationService {

    void sendNotification(NotificationDto dto);
}
