package ru.yandex.practicum.transferservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TransferMetrics {

    private final MeterRegistry meterRegistry;

    public TransferMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementFailedTransfer(String fromLogin, String toLogin) {
        Counter.builder("bank.transfer.failed")
                .description("Number of failed transfer attempts")
                .tag("from_login", fromLogin)
                .tag("to_login", toLogin)
                .register(meterRegistry)
                .increment();
    }
}
