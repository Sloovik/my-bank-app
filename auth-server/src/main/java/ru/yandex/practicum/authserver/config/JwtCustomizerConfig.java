package ru.yandex.practicum.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

@Configuration
public class JwtCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                var principal = context.getPrincipal();

                context.getClaims().claim("preferred_username", principal.getName());

                if ("client_credentials".equals(context.getAuthorizationGrantType().getValue())) {
                    var scopes = context.getAuthorizedScopes().stream()
                            .collect(Collectors.toList());
                    context.getClaims().claim("authorities", scopes);
                } else {
                    var authorities = principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    context.getClaims().claim("authorities", authorities);
                }
            }
        };
    }
}
