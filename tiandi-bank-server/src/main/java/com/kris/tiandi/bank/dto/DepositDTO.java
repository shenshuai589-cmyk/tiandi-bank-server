package com.kris.tiandi.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositDTO {

    @NotNull(message = "用户Id不能为空")
    private Long userId;

    @NotNull(message = "存款金额不能为空")
    @DecimalMin(value = "0.01", message = "存款金额必须大于0")
    private BigDecimal amount;
}
