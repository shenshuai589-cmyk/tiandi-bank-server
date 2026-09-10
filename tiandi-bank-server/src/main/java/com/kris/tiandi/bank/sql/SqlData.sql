CREATE DATABASE tiandi_bank
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE tiandi_bank;

CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                          username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                          password VARCHAR(100) NOT NULL COMMENT '密码',
                          real_name VARCHAR(50) COMMENT '真实姓名',
                          phone VARCHAR(20) COMMENT '手机号',
                          role TINYINT DEFAULT 0 COMMENT '0普通用户 1管理员',
                          status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);



CREATE TABLE bank_account (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
                              user_id BIGINT NOT NULL COMMENT '用户ID',
                              account_no VARCHAR(30) NOT NULL UNIQUE COMMENT '银行卡号',
                              balance DECIMAL(15,2) DEFAULT 0.00 COMMENT '余额',
                              status TINYINT DEFAULT 1 COMMENT '0冻结 1正常',
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开户时间'
);


CREATE TABLE bank_transaction (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易ID',
                                  user_id BIGINT NOT NULL COMMENT '用户ID',
                                  account_id BIGINT NOT NULL COMMENT '账户ID',
                                  type TINYINT NOT NULL COMMENT '1存款 2取款 3转账转出 4转账转入',
                                  amount DECIMAL(15,2) NOT NULL COMMENT '交易金额',
                                  before_balance DECIMAL(15,2) COMMENT '交易前余额',
                                  after_balance DECIMAL(15,2) COMMENT '交易后余额',
                                  target_account_no VARCHAR(30) COMMENT '目标账户',
                                  remark VARCHAR(255) COMMENT '备注',
                                  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间'
);


