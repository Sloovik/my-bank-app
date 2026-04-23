package ru.yandex.practicum.notificationsservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.notificationsservice.dto.NotificationDto;
import ru.yandex.practicum.notificationsservice.model.Notification;
import ru.yandex.practicum.notificationsservice.repository.NotificationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendNotification_shouldSaveToRepository() {
        when(notificationRepository.save(any())).thenReturn(new Notification());

        NotificationDto dto = new NotificationDto("user1", "Test message");
        notificationService.sendNotification(dto);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
