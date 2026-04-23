package ru.yandex.practicum.cashservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.cashservice.config.FeignOAuth2Config;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-service", configuration = FeignOAuth2Config.class)
public interface NotificationsClient {

    @PostMapping("/notifications")
    void sendNotification(@RequestBody NotificationDto notificationDto);
}
