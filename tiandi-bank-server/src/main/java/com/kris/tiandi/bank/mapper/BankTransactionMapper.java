package com.kris.tiandi.bank.mapper;

import com.kris.tiandi.bank.pojo.BankTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BankTransactionMapper {

    int insert(BankTransaction transaction);
    /**
     * 查询指定用户的交易记录
     */
    List<BankTransaction> selectByUserId(@Param("userId") Long userId);
}
