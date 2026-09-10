package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.mapper.UserMapper;
import com.kris.tiandi.bank.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final UserMapper userMapper;

    public TestController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/test/user")
    public User getUser(@RequestParam String username){
        return userMapper.selectByUsername(username);
    }
}
