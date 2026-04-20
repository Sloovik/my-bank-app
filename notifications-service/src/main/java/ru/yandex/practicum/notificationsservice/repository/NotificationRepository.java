package ru.yandex.practicum.notificationsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.notificationsservice.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
