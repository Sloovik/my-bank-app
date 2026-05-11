package ru.yandex.practicum.notificationsservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {

    private final MeterRegistry meterRegistry;

    public NotificationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementFailedNotification(String login) {
        Counter.builder("bank.notification.failed")
                .description("Number of failed notification delivery attempts")
                .tag("login", login)
                .register(meterRegistry)
                .increment();
    }
}
