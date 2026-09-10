package com.kris.tiandi.bank.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankTransaction {

    private Integer id;

    /**
     * 交易所属用户
     */
    private Long userId;

    private Long accountId;

    /**交易类型
     * 1 存款
     * 2 取款
     * 3 转账转出
     * 4 转账转入
     */

    private Integer type;
    /**
     * 交易金额
     */

    private BigDecimal amount;


    private BigDecimal beforeBalance;

    private BigDecimal afterBalance;

    private String targetAccountNo;

    private String remark;

    private LocalDateTime createTime;

}
