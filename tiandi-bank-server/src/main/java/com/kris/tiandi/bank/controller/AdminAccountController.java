package com.kris.tiandi.bank.controller;


import com.kris.tiandi.bank.common.Result;
import com.kris.tiandi.bank.pojo.BankAccount;
import com.kris.tiandi.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {

    @Autowired
    private AccountService  accountService;

    @GetMapping("/list")
    public Result<List<BankAccount>> list() {
        List<BankAccount> accounts = accountService.listAll();
        return Result.success(accounts);
    }
}
