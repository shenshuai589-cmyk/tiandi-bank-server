package com.kris.tiandi.bank.config;


import com.kris.tiandi.bank.interceptor.LoginInterceptor;
import com.kris.tiandi.bank.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 普通用户登录校验
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register");

        //管理员用户登录校验
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/admin/**");
    }
}
