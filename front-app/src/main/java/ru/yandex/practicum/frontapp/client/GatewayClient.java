package ru.yandex.practicum.frontapp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.frontapp.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GatewayClient {

    @Value("${gateway.url:http://localhost:8765}")
    private String gatewayBaseUrl;

    private final RestTemplate restTemplate;

    private HttpHeaders bearerHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public AccountDto getAccount(String login, String accessToken) {
        String url = gatewayBaseUrl + "/api/accounts/" + login;
        HttpEntity<Void> entity = new HttpEntity<>(bearerHeaders(accessToken));
        ResponseEntity<AccountDto> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, AccountDto.class);
        return response.getBody();
    }

    public AccountDto updateAccount(String login, String name, LocalDate birthdate, String accessToken) {
        String url = gatewayBaseUrl + "/api/accounts/" + login;

        Map<String, Object> requestBody = Map.of(
                "name", name,
                "birthdate", birthdate.toString()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, bearerHeaders(accessToken));
        ResponseEntity<AccountDto> response = restTemplate.exchange(
                url, HttpMethod.PUT, entity, AccountDto.class);
        return response.getBody();
    }

    public List<AccountShortDto> getOtherAccounts(String excludeLogin, String accessToken) {
        String url = gatewayBaseUrl + "/api/accounts?excludeLogin=" + excludeLogin;
        HttpEntity<Void> entity = new HttpEntity<>(bearerHeaders(accessToken));
        ResponseEntity<List<AccountShortDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<AccountShortDto>>() {});
        return response.getBody();
    }

    public CashResponseDto processCash(String login, CashRequestDto req, String accessToken) {
        String url = gatewayBaseUrl + "/api/cash/" + login;
        HttpEntity<CashRequestDto> entity = new HttpEntity<>(req, bearerHeaders(accessToken));
        ResponseEntity<CashResponseDto> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, CashResponseDto.class);
        return response.getBody();
    }

    public void processTransfer(TransferRequestDto req, String accessToken) {
        String url = gatewayBaseUrl + "/api/transfer";
        HttpEntity<TransferRequestDto> entity = new HttpEntity<>(req, bearerHeaders(accessToken));
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }
}
