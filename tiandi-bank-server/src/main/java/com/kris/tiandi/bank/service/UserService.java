package com.kris.tiandi.bank.service;

import com.kris.tiandi.bank.dto.LoginDTO;
import com.kris.tiandi.bank.dto.RegisterDTO;
import com.kris.tiandi.bank.pojo.User;
import com.kris.tiandi.bank.vo.LoginVO;

import java.util.List;

public interface UserService {

    void register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);

    List<User> listAll();

    void freezeUser(Long userId);

    void unfreezeUser(Long userId);
}
