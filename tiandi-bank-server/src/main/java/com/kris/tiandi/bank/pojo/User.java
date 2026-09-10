package com.kris.tiandi.bank.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    /**
     * 0 普通用户
     * 1 管理员
     */
    private Integer role;

    /**
     * 0 禁用
     * 1 正常
     */
    private Integer status;

    private LocalDateTime createTime;
}
