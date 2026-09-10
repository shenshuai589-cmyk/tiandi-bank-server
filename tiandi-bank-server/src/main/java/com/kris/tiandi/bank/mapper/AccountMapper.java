package com.kris.tiandi.bank.mapper;


import com.kris.tiandi.bank.pojo.BankAccount;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface AccountMapper {

    BankAccount selectByUserId(Long userId);


    int insert(BankAccount account);

    int updateBalance(Long userId, BigDecimal amount);

    int withdrawBalance(Long userId, BigDecimal amount);
}
