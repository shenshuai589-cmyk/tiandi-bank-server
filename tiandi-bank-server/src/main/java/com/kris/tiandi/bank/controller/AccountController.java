package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.dto.DepositDTO;
import com.kris.tiandi.bank.dto.TransferDTO;
import com.kris.tiandi.bank.dto.WithdrawDTO;
import com.kris.tiandi.bank.pojo.BankAccount;
import com.kris.tiandi.bank.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 开户
     */
    @PostMapping("/open")
    public BankAccount openAccount(@RequestParam("userId") Long id){
        return accountService.openAccount(id);
    }

    /**
     * 查询账户
     */
    @GetMapping("/info")
    public BankAccount getAccoun(@RequestParam Long id){
        return accountService.getAccount(id);
    }

    @PostMapping("/deposit")
    public String deposit(@Valid @RequestBody DepositDTO depositDTO){
        accountService.deposit(depositDTO);
        return "存款成功";
    }


    @PostMapping("/withdraw")
    public String withdraw(@Valid @RequestBody WithdrawDTO withdrawDTO){
        accountService.withdraw(withdrawDTO);

        return "取款成功";
    }

    @PostMapping("/transfer")
    public String transfer(@Valid @RequestBody TransferDTO transferDTO){
        accountService.transfer(transferDTO);

        return "转账成功";
    }


}
