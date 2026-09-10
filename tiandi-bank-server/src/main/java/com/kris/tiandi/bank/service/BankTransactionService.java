package com.kris.tiandi.bank.service;

import com.kris.tiandi.bank.pojo.BankTransaction;

import java.util.List;

public interface BankTransactionService {

    void record(BankTransaction transaction);
    /**
     * 查询指定用户的交易记录
     */
    List<BankTransaction> listByUserId(Long userId);
}
