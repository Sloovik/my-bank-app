package ru.yandex.practicum.accountsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.accountsservice.dto.AccountResponseDto;
import ru.yandex.practicum.accountsservice.dto.BalanceUpdateDto;
import ru.yandex.practicum.accountsservice.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(AccountControllerIntegrationTest.TestSecurityConfig.class)
class AccountControllerIntegrationTest {

    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .httpBasic(basic -> {})
                    .csrf(csrf -> csrf.disable());
            return http.build();
        }
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AccountService accountService;
    @Test
    @WithMockUser(authorities = {"SCOPE_ACCOUNTS_READ"})
    void getAccount_shouldReturn200() throws Exception {
        AccountResponseDto dto = new AccountResponseDto(1L, "user1", "Иванов Иван",
                LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000));
        when(accountService.getAccount("user1")).thenReturn(dto);
        mockMvc.perform(get("/accounts/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user1"))
                .andExpect(jsonPath("$.balance").value(1000));
    }
    @Test
    @WithMockUser(authorities = {"SCOPE_ACCOUNTS_WRITE"})
    void updateBalance_shouldReturn200() throws Exception {
        BalanceUpdateDto updateDto = new BalanceUpdateDto(BigDecimal.valueOf(500));
        AccountResponseDto responseDto = new AccountResponseDto(1L, "user1", "Иванов Иван",
                LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1500));
        when(accountService.updateBalance("user1", BigDecimal.valueOf(500))).thenReturn(responseDto);
        mockMvc.perform(patch("/accounts/user1/balance")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }
    @Test
    void getAccount_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/accounts/user1"))
                .andExpect(status().isUnauthorized());
    }
}
