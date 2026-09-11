package com.kris.tiandi.bank.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result() {

    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功
    public static <T>  Result<T> success(T data) {
        return new Result<>(200,"success",data);
    }

    // 成功，但是没有返回数据
    public static <T>  Result<T> success() {
        return new Result<>(200,"success",null);
    }

    // 失败
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

}
