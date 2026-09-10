package com.kris.tiandi.bank.service.impl;

import com.kris.tiandi.bank.mapper.BankTransactionMapper;
import com.kris.tiandi.bank.pojo.BankTransaction;
import com.kris.tiandi.bank.service.BankTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankTransactionServiceImpl implements BankTransactionService {

    @Autowired
    private BankTransactionMapper bankTransactionMapper;


    @Override
    public void record(BankTransaction transaction) {
        bankTransactionMapper.insert(transaction);
    }

    @Override
    public List<BankTransaction> listByUserId(Long userId) {
        return bankTransactionMapper.selectByUserId(userId);
    }

}
