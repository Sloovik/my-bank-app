package ru.yandex.practicum.accountsservice.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignResilienceConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 422) {
                return new IllegalStateException("Insufficient funds");
            }
            return new RuntimeException("Feign error: " + response.status());
        };
    }
}
