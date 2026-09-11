package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.common.Result;
import com.kris.tiandi.bank.dto.LoginDTO;
import com.kris.tiandi.bank.dto.RegisterDTO;
import com.kris.tiandi.bank.pojo.User;
import com.kris.tiandi.bank.service.UserService;
import com.kris.tiandi.bank.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);

        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {

        return Result.success(userService.login(loginDTO));
    }
}
