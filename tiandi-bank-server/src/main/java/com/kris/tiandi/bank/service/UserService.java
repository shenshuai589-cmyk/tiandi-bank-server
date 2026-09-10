package com.kris.tiandi.bank.service;

import com.kris.tiandi.bank.dto.LoginDTO;
import com.kris.tiandi.bank.dto.RegisterDTO;
import com.kris.tiandi.bank.vo.LoginVO;

public interface UserService {

    void register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);
}
