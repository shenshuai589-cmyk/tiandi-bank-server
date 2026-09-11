package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.common.Result;
import com.kris.tiandi.bank.mapper.BankTransactionMapper;
import com.kris.tiandi.bank.pojo.BankTransaction;
import com.kris.tiandi.bank.service.BankTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/transaction")
public class AdminBankTranscationController {

    @Autowired
    private BankTransactionService bankTransactionService;
    
    @GetMapping("/list")
    public Result<List<BankTransaction>> list(){
        List<BankTransaction> transactions = bankTransactionService.listAll();
        return Result.success(transactions);
    }
}
