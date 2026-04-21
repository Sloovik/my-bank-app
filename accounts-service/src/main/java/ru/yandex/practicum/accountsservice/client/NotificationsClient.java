package ru.yandex.practicum.accountsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.accountsservice.config.FeignOAuth2Config;
import ru.yandex.practicum.accountsservice.dto.NotificationDto;

@FeignClient(name = "notifications-service",
        configuration = FeignOAuth2Config.class,
        fallback = NotificationsClientFallback.class)
public interface NotificationsClient {

    @PostMapping("/notifications")
    void sendNotification(@RequestBody NotificationDto dto);
}
