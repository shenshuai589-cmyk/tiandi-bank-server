package com.kris.tiandi.bank.interceptor;

import com.kris.tiandi.bank.common.UserContext;
import com.kris.tiandi.bank.mapper.UserMapper;
import com.kris.tiandi.bank.pojo.User;
import com.kris.tiandi.bank.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1.从请求头获取token
        String token = request.getHeader("Authorization");
        //2.判断token是否存在
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("请先登录");
        }

        // 3.去掉Bearer 前缀
        if (token.startsWith("Bearer ")) {
             token = token.substring(7);
        }

        // 4.接续Token，获取userId
        Long userId;

        try{
            userId = jwtUtil.getUserId(token);

        }catch (Exception e){
            throw new Exception("登录凭证已失效，请重新登录");
        }

        // 5.查询用户
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 6.判断是否是管理员
        if (user.getRole() != 1) {
            throw new RuntimeException("没有管理员权限");
        }

        // 7.保存到ThreadLocal
        UserContext.setUserId(userId);

        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        UserContext.remove();
    }
}
