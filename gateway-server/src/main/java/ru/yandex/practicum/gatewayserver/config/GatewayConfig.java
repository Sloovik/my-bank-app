package ru.yandex.practicum.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("accounts-service", r -> r
                        .path("/api/accounts/**")
                        .filters(f -> f
                                .rewritePath("/api/accounts/(?<segment>.*)", "/${segment}")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://accounts-service"))
                .route("cash-service", r -> r
                        .path("/api/cash/**")
                        .filters(f -> f
                                .rewritePath("/api/cash/(?<segment>.*)", "/${segment}")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://cash-service"))
                .route("transfer-service", r -> r
                        .path("/api/transfer/**")
                        .filters(f -> f
                                .rewritePath("/api/transfer/(?<segment>.*)", "/${segment}")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://transfer-service"))
                .route("notifications-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .rewritePath("/api/notifications/(?<segment>.*)", "/${segment}")
                                .removeRequestHeader("Cookie"))
                        .uri("lb://notifications-service"))
                .build();
    }
}
