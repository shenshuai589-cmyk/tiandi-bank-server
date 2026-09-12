package com.kris.tiandi.bank.interceptor;

import com.kris.tiandi.bank.common.UserContext;
import com.kris.tiandi.bank.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token != null || token.isEmpty()) {
            throw new RuntimeException("请先登录");
        }

        // 去掉Bearer
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId;

        try{
            userId = jwtUtil.getUserId(token);

        }catch (Exception e){
            throw new RuntimeException("登录凭证已失效，请重新登录");
        }

        UserContext.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        UserContext.remove();
    }
}
