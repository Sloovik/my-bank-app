package ru.yandex.practicum.transferservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.yandex.practicum.transferservice.dto.NotificationDto;
import ru.yandex.practicum.transferservice.event.NotificationEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationProducer {

    private static final String TOPIC = "bank-notifications";

    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationEvent(NotificationEvent event) {
        send(event.login(), event.message());
    }

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