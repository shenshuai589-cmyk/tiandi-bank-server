package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.common.Result;
import com.kris.tiandi.bank.dto.DepositDTO;
import com.kris.tiandi.bank.dto.TransferDTO;
import com.kris.tiandi.bank.dto.WithdrawDTO;
import com.kris.tiandi.bank.pojo.BankAccount;
import com.kris.tiandi.bank.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 开户
     */
    @PostMapping("/open")
    public Result<BankAccount> openAccount(@RequestParam("userId")
                                               @NotNull(message = "用户Id不能为空") Long id){
        BankAccount account = accountService.openAccount(id);
        return Result.success(account);
    }

    /**
     * 查询账户
     */
    @GetMapping("/info")
    public Result<BankAccount> getAccoun(@RequestParam("userId")
                                             @NotNull(message = "用户Id不能为空") Long userId){
        BankAccount account = accountService.getAccount(userId);

        return Result.success(account);
    }

    @PostMapping("/deposit")
    public Result<String> deposit(@Valid @RequestBody DepositDTO depositDTO){
        accountService.deposit(depositDTO);
        return Result.success();
    }


    @PostMapping("/withdraw")
    public Result<String> withdraw(@Valid @RequestBody WithdrawDTO withdrawDTO){
        accountService.withdraw(withdrawDTO);

        return Result.success();
    }

    @PostMapping("/transfer")
    public Result<String> transfer(@Valid @RequestBody TransferDTO transferDTO){
        accountService.transfer(transferDTO);

        return Result.success();
    }





}
