package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * @ClassName UserLoginServiceImpl
 * @Description
 * @Author 传智播客
 * @Date 12:15 2020/9/15
 * @Version 2.1
 **/
@Service
public class UserLoginServiceImpl implements UserLoginService{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;  // 之前：http://eureka-provider-client/xxx

    /**
     * @author 栗子
     * @Description
     * @Date 12:22 2020/9/15
     * @param username      账号
     * @param password      密码
     * @param grant_type    授权方式
     * @param clientId      客户端ID
     * @param clientSecret  客户端秘钥
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    @Override
    public AuthToken login(String username, String password, String grant_type, String clientId, String clientSecret) {
        // 生成token的请求地址
//        String url = "http://localhost:9001/oauth/token";
        ServiceInstance serviceInstance = discoveryClient.getInstances("user-auth").get(0);
        String uri = serviceInstance.getUri().toString();   // http://ip:port
        String url = uri + "/oauth/token";

        // 发送请求，调用该服务
        // requestEntity：封装请求头和请求体
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap();
        body.add("grant_type", grant_type);
        body.add("username", username);
        body.add("password", password);
        

        MultiValueMap headers = new LinkedMultiValueMap();
        String encode = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.add("Authorization", "Basic " + encode);
        HttpEntity requestEntity = new HttpEntity(body, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        Map<String, Object> map = responseEntity.getBody();

        // 获取响应结果
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(map.get("access_token").toString());
        authToken.setRefreshToken(map.get("refresh_token").toString());
        authToken.setJti(map.get("jti").toString());
        return authToken;
    }
}
