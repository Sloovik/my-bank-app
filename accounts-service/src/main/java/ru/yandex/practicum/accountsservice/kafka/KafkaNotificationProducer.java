package ru.yandex.practicum.accountsservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.accountsservice.dto.NotificationDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationProducer {

    private static final String TOPIC = "bank-notifications";

    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public void send(String login, String message) {
        NotificationDto dto = new NotificationDto(login, message);
        kafkaTemplate.send(TOPIC, login, dto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send notification for {}: {}", login, ex.getMessage());
                    } else {
                        log.debug("Notification sent for {}: offset={}", login,
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
