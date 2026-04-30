package ru.yandex.practicum.notificationsservice.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.notificationsservice.dto.NotificationDto;
import ru.yandex.practicum.notificationsservice.service.NotificationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationKafkaConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationKafkaConsumer consumer;

    @Test
    void consume_shouldCallNotificationService() {
        NotificationDto dto = new NotificationDto("user1", "Тестовое уведомление");

        consumer.consume(dto);

        verify(notificationService, times(1)).sendNotification(dto);
    }
}