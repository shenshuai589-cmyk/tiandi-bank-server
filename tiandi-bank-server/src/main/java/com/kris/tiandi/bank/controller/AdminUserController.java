package com.kris.tiandi.bank.controller;

import com.kris.tiandi.bank.common.Result;
import com.kris.tiandi.bank.pojo.User;
import com.kris.tiandi.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> list(){
        List<User> users = userService.listAll();
        return Result.success(users);
    }


    @PutMapping("/{userId}/freeze")
    public Result<?> freeze(@PathVariable Long userId){
        userService.freezeUser(userId);

        return Result.success();
    }
    @PutMapping("/{userId}/unfreeze")
    public Result<?> unfreeze(@PathVariable Long userId){
        userService.unfreezeUser(userId);
        return Result.success();
    }
}
