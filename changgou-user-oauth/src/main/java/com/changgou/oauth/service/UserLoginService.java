package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

import java.util.Map;

/**
 * @ClassName UserLoginService
 * @Description
 * @Author 传智播客
 * @Date 12:15 2020/9/15
 * @Version 2.1
 **/
public interface UserLoginService {

    /**
     * @author 栗子
     * @Description
     * @Date 12:22 2020/9/15
     * @param username      账号
     * @param password      密码
     * @param grant_type    授权方式
     * @param clientId      客户端ID
     * @param clientSecret  客户端秘钥
     * @return AuthToken
     **/
    AuthToken login(String username, String password, String grant_type, String clientId, String clientSecret);
}
