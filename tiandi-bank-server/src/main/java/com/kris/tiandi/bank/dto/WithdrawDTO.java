package com.kris.tiandi.bank.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawDTO {

    @NotNull(message = "用户Id不能为空")
    private Long userId;

    @NotNull(message = "取款金额不能为空")
    @DecimalMin(value = "0.01", message = "取款金额必须大于0")
    private BigDecimal amount;
}


