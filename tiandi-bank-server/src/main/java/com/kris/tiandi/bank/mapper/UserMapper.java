package com.kris.tiandi.bank.mapper;


import com.kris.tiandi.bank.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectByUsername(String username);

    int insert(User user);
}
