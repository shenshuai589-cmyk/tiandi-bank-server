package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.pojo.BankTransaction;
import com.kris.tiandi.bank.service.BankTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class BankTransactionController {

    @Autowired
    private BankTransactionService  bankTransactionService;

    @GetMapping("/list")
    public List<BankTransaction> list(@RequestParam Long userId) {

        return bankTransactionService.listByUserId(userId);

    }
}
