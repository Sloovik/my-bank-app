package ru.yandex.practicum.notificationsservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.notificationsservice.dto.NotificationDto;
import ru.yandex.practicum.notificationsservice.metrics.NotificationMetrics;
import ru.yandex.practicum.notificationsservice.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;
    private final NotificationMetrics notificationMetrics;

    @KafkaListener(
            topics = "bank-notifications",
            groupId = "notifications-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(NotificationDto dto) {
        log.info("Received notification from Kafka: login={}", dto.login());
        try {
            notificationService.sendNotification(dto);
            log.debug("Notification processed successfully for login={}", dto.login());
        } catch (Exception ex) {
            log.error("Failed to process notification for login={}: {}", dto.login(), ex.getMessage(), ex);
            notificationMetrics.incrementFailedNotification(dto.login());
        }
    }
}
