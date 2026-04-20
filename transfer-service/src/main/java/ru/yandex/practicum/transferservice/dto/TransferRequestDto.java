package ru.yandex.practicum.transferservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    @NotBlank
    private String fromLogin;

    @NotBlank
    private String toLogin;

    @NotNull
    @Positive
    private BigDecimal amount;
}
