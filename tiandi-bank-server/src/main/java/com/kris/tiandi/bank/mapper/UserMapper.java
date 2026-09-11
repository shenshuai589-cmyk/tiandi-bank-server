package com.kris.tiandi.bank.mapper;


import com.kris.tiandi.bank.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectByUsername(String username);

    int insert(User user);

    List<User> selectAll();

    User selectById(Long id);

    int updateStatus(Long id,Integer status);
}
