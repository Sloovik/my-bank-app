package ru.yandex.practicum.accountsservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.accountsservice.dto.NotificationDto;

@Slf4j
@Component
public class NotificationsClientFallback implements NotificationsClient {
    @Override
    public void sendNotification(NotificationDto dto) {
        log.warn("Notifications service unavailable, notification dropped: login={}, message={}",
                dto.login(), dto.message());
    }
}
