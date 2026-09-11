package com.kris.tiandi.bank.service.impl;

import com.kris.tiandi.bank.dto.LoginDTO;
import com.kris.tiandi.bank.dto.RegisterDTO;
import com.kris.tiandi.bank.mapper.UserMapper;
import com.kris.tiandi.bank.service.UserService;

import com.kris.tiandi.bank.pojo.User;
import com.kris.tiandi.bank.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserMapper  userMapper;


    @Override
    public void register(RegisterDTO registerDTO) {
        // 1. 查询用户名是否已经存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());

        if (existUser != null) {
            throw new RuntimeException("用户名已经存在");
        }

        //用户名不存在，创建用户
        User user = new com.kris.tiandi.bank.pojo.User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());


        // 普通用户
        user.setRole(0);

        // 正常状态
        user.setStatus(1);

        // 3.保存到数据库
        userMapper.insert(user);

    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());

        // 用户名不存在
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 密码错误
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4.用户被禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        return new LoginVO(user.getId(), user.getUsername(), user.getRealName());
    }

    @Override
    public List<User> listAll() {

        return userMapper.selectAll();
    }

    //冻结账户
    @Override
    public void freezeUser(Long userId) {
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已经被冻结");
        }

        int rows = userMapper.updateStatus(userId, 0);

        if (rows == 0) {
            throw new RuntimeException("冻结用户失败");
        }

    }

    @Override
    public void unfreezeUser(Long userId) {
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 1) {
            throw new RuntimeException("用户当前未被冻结");
        }

        int rows = userMapper.updateStatus(userId, 1);

        if (rows == 0) {
            throw new RuntimeException("解冻用户失败");
        }
    }
}
