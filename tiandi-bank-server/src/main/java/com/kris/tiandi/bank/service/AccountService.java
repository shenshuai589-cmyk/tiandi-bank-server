package com.kris.tiandi.bank.service;

import com.kris.tiandi.bank.dto.DepositDTO;
import com.kris.tiandi.bank.dto.TransferDTO;
import com.kris.tiandi.bank.dto.WithdrawDTO;
import com.kris.tiandi.bank.pojo.BankAccount;

public interface AccountService {

    BankAccount openAccount(Long userId);

    BankAccount getAccount(Long userId);

    void deposit(DepositDTO depositDTO);

    void withdraw(WithdrawDTO withdrawDTO);

    void transfer(TransferDTO transferDTO);
}
