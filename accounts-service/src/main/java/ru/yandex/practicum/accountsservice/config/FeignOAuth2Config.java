package ru.yandex.practicum.accountsservice.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignOAuth2Config {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Bean
    public RequestInterceptor oauth2RequestInterceptor() {
        return requestTemplate -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("accounts-service")
                    .principal("accounts-service")
                    .build();

            var authorizedClient = authorizedClientManager.authorize(authorizeRequest);
            if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                String token = authorizedClient.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            } else {
                log.warn("Could not obtain OAuth2 access token for notifications-service");
            }
        };
    }
}
