package ru.yandex.practicum.accountsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.accountsservice.client.NotificationsClient;
import ru.yandex.practicum.accountsservice.dto.NotificationDto;
import ru.yandex.practicum.accountsservice.model.OutboxEvent;
import ru.yandex.practicum.accountsservice.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxWorker {

    private final OutboxEventRepository outboxRepository;
    private final NotificationsClient notificationsClient;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : events) {
            try {
                String payload = event.getPayload();
                String login = extractField(payload, "login");
                String message = extractField(payload, "message");

                notificationsClient.sendNotification(new NotificationDto(login, message));

                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(event);
                log.debug("Outbox event {} processed", event.getId());
            } catch (Exception ex) {
                log.warn("Failed to process outbox event {}: {}", event.getId(), ex.getMessage());
            }
        }
    }

    private String extractField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
