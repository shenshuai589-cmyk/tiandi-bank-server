package com.kris.tiandi.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    @NotNull(message = "转出用户Id不能为空")
    private Long fromUserId;

    @NotNull(message = "转入用户Id不能为空")
    private Long toUserId;

    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额必须大于0")
    private BigDecimal amount;
}
