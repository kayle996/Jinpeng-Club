package com.changgou.oauth.controller;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName UserLoginController
 * @Description 用户登录
 * @Author 传智播客
 * @Date 12:11 2020/9/15
 * @Version 2.1
 **/
@RestController
@RequestMapping("/user")
public class UserLoginController {

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Autowired
    private UserLoginService userLoginService;

    /**
     * @author 栗子
     * @Description 用户登录
     * @Date 12:22 2020/9/15
     * @param username
     * @param password
     * @return entity.Result<java.util.Map<java.lang.String,java.lang.String>>
     **/
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response){
        try {
            // 授权方式：默认，密码授权
            String grant_type = "password";
            AuthToken authToken = userLoginService.login(username, password, grant_type, clientId, clientSecret);

            // 将token存储到cookie中
            Cookie cookie = new Cookie("Authorization", authToken.getAccessToken());
            cookie.setPath("/");
            cookie.setDomain("localhost");
            response.addCookie(cookie);
            return new Result<>(true, StatusCode.OK, "登录成功", authToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.LOGINERROR, "登录失败");
    }
}
