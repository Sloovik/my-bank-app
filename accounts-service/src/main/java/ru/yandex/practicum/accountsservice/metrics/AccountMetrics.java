package ru.yandex.practicum.accountsservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AccountMetrics {

    private final MeterRegistry meterRegistry;

    public AccountMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementFailedWithdrawal(String login) {
        Counter.builder("bank.withdrawal.failed")
                .description("Number of failed withdrawal attempts")
                .tag("login", login)
                .register(meterRegistry)
                .increment();
    }
}
