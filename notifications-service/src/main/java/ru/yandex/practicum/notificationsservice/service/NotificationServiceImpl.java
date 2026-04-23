package ru.yandex.practicum.notificationsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.notificationsservice.dto.NotificationDto;
import ru.yandex.practicum.notificationsservice.model.Notification;
import ru.yandex.practicum.notificationsservice.repository.NotificationRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(NotificationDto dto) {
        Notification notification = Notification.builder()
                .login(dto.login())
                .message(dto.message())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("NOTIFICATION [{}]: {}", dto.login(), dto.message());
    }
}
