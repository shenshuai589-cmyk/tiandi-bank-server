package com.kris.tiandi.bank.service.impl;

import com.kris.tiandi.bank.dto.DepositDTO;
import com.kris.tiandi.bank.dto.TransferDTO;
import com.kris.tiandi.bank.dto.WithdrawDTO;
import com.kris.tiandi.bank.mapper.AccountMapper;
import com.kris.tiandi.bank.mapper.BankTransactionMapper;
import com.kris.tiandi.bank.pojo.BankAccount;
import com.kris.tiandi.bank.pojo.BankTransaction;
import com.kris.tiandi.bank.service.AccountService;
import com.kris.tiandi.bank.service.BankTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BankTransactionMapper bankTransactionMapper;
    @Autowired
    private BankTransactionService bankTransactionService;

    @Override
    public BankAccount openAccount(Long userId) {

        // 1. 查询用户是否已经开户
        BankAccount existAccount = accountMapper.selectByUserId(userId);
        if (existAccount != null) {
            throw new RuntimeException("该用户已经开户");
        }

        // 2.创建账户
        BankAccount account = new BankAccount();

        account.setUserId(userId);

        String accountNo = "622200" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10);

        account.setAccountNo(accountNo);

    // 初始余额为0
        account.setBalance(BigDecimal.ZERO);

        // 账户正常
        account.setStatus(1);

        accountMapper.insert(account);

        return account;
    }

    @Override
    public BankAccount getAccount(Long userId) {

        BankAccount account = accountMapper.selectByUserId(userId);

        if (account == null) {
            throw new RuntimeException("您还没有银行账户");
        }
        return account;
    }

    @Override
    @Transactional
    public void deposit(DepositDTO depositDTO) {
        // 1.查询账户
        BankAccount account = accountMapper.selectByUserId(depositDTO.getUserId());

        if (account == null) {
            throw new RuntimeException("您好没有银行账户");
        }
        
        // 2.判断账户状态
        if (account.getStatus() == 0) {
            throw new RuntimeException("该账户已被冻结");
        }

        // 记录转帐前的余额
        BigDecimal beforeBalance = account.getBalance();
        
        // 3.更新余额
        int rows = accountMapper.updateBalance(depositDTO.getUserId(), depositDTO.getAmount());

        // 记录充值之后的钱
        BigDecimal afterBalance = beforeBalance.add(depositDTO.getAmount());
        
        // 创建交易流水
        BankTransaction transaction = new BankTransaction();

        transaction.setUserId(account.getUserId());
        transaction.setAccountId(account.getId());
        transaction.setType(1);
        transaction.setAmount(depositDTO.getAmount());
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setRemark("账户存款");

        // 保存交易流水
        bankTransactionService.record(transaction);
    }

    @Override
    @Transactional
    public void withdraw(WithdrawDTO withdrawDTO) {
        // 1. 查询账户
        BankAccount account = accountMapper.selectByUserId(withdrawDTO.getUserId());


        if (account == null) {
            throw new RuntimeException("您还没有银行账户");
        }

        // 2.判断账户状态
        if (account.getStatus() == 0) {
            throw new RuntimeException("该账户已被冻结");
        }

        BigDecimal beforeBalance = account.getBalance();

        //3. 扣除余额
        int rows = accountMapper.withdrawBalance(withdrawDTO.getUserId(), withdrawDTO.getAmount());



        if(rows == 0){
            throw new RuntimeException("余额不足");
        }

        // 利用原余额调用BigDecimal的subtract减去取的钱数得到取钱后的余额
        BigDecimal afterBalance = beforeBalance.subtract(withdrawDTO.getAmount());

        BankTransaction transaction = new BankTransaction();
        transaction.setUserId(account.getUserId());
        transaction.setAccountId(account.getId());
        transaction.setType(2);
        transaction.setAmount(withdrawDTO.getAmount());
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setRemark("账户取款");

        bankTransactionService.record(transaction);

    }

    @Override
    @Transactional
    public void transfer(TransferDTO transferDTO) {
        Long fromUserId = transferDTO.getFromUserId();
        Long toUserId = transferDTO.getToUserId();
        BigDecimal amount = transferDTO.getAmount();

        // 1. 不能给自己转账
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能给自己转账");
        }

        // 2. 查询转出方账户
        BankAccount fromAccount =
                accountMapper.selectByUserId(fromUserId);

        if (fromAccount == null) {
            throw new RuntimeException("转出方没有银行账户");
        }

        // 3. 查询转入方账户
        BankAccount toAccount =
                accountMapper.selectByUserId(toUserId);

        if (toAccount == null) {
            throw new RuntimeException("转入方没有银行账户");
        }
        // 4. 检查账户状态
        if (fromAccount.getStatus() == 0) {
            throw new RuntimeException("转出方账户已被冻结");
        }

        if (toAccount.getStatus() == 0) {
            throw new RuntimeException("转入方账户已被冻结");
        }

        // 转帐前转出账户的原始余额
        BigDecimal fromBeforeBalance  = fromAccount.getBalance();
        // 转帐前转入账户的原始余额
        BigDecimal toBeforeBalance = toAccount.getBalance();


        // 5. 扣除转出方余额
        int withdrawRows =
                accountMapper.withdrawBalance(fromUserId, amount);

        if (withdrawRows == 0) {
            throw new RuntimeException("余额不足");
        }

        // 6. 增加转入方余额
        int depositRows =
                accountMapper.updateBalance(toUserId, amount);

        if (depositRows == 0) {
            throw new RuntimeException("转账失败");
        }
        // 转帐后转出账户的原始余额
        BigDecimal fromAfterBalance = fromBeforeBalance.subtract(amount);
        // 转帐后转入账户的原始余额
        BigDecimal toAfterBalance = toBeforeBalance.add(amount);

        // 转出流水
        BankTransaction fromTransaction  = new BankTransaction();

        fromTransaction.setUserId(fromAccount.getUserId());
        fromTransaction.setAccountId(fromAccount.getId());
        fromTransaction.setType(3);
        fromTransaction.setAmount(amount);
        fromTransaction.setBeforeBalance(fromBeforeBalance);
        fromTransaction.setAfterBalance(fromAfterBalance);
        fromTransaction.setTargetAccountNo(toAccount.getAccountNo());
        fromTransaction.setRemark("转账支出");

        bankTransactionService.record(fromTransaction);


        // 转入流水
        BankTransaction toTransaction = new BankTransaction();

        toTransaction.setUserId(toAccount.getUserId());
        toTransaction.setAccountId(toAccount.getId());
        toTransaction.setType(4);
        toTransaction.setAmount(amount);
        toTransaction.setBeforeBalance(toBeforeBalance);
        toTransaction.setAfterBalance(toAfterBalance);
        toTransaction.setTargetAccountNo(fromAccount.getAccountNo());
        toTransaction.setRemark("转账收入");

        bankTransactionService.record(toTransaction);





    }
}
