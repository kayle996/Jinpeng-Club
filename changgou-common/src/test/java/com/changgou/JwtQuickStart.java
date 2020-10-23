package com.changgou;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JwtQuickStart
 * @Description jwt入门程序
 * @Author 传智播客
 * @Date 11:20 2020/9/14
 * @Version 2.1
 **/
public class JwtQuickStart {

    // 生成token
    @Test
    public void testCreateToken(){
        // 1、获取创建jwt的对象
        JwtBuilder jwtBuilder = Jwts.builder();

        // 2、设置jwt的组成部分
        // 2.1 头部
        Map<String, Object> header = new HashMap<>();
        header.put("address", "广东深圳");
        header.put("school", "五道口职业技术学院");
        jwtBuilder.setHeader(header);
        // 2.2 载荷
        jwtBuilder.setIssuer("tom");    // 签发者
        jwtBuilder.setId("001");        // 唯一标识
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + 20000)); // 设置20s后过期
        // 2.3 签证
        jwtBuilder.signWith(SignatureAlgorithm.HS256, "itheima");

        // 3、生成token
        String jwt = jwtBuilder.compact();
        System.out.println("token:" + jwt);

    }

    // 解析token
    @Test
    public void testParserToken(){
        String token = "eyJhZGRyZXNzIjoi5bm_5Lic5rex5ZyzIiwic2Nob29sIjoi5LqU6YGT5Y-j6IGM5Lia5oqA5pyv5a2m6ZmiIiwiYWxnIjoiSFMyNTYifQ.eyJyb2xlIjoicm9sZV9hZG1pbiIsIm5hbWUiOiJ0b20iLCJpZCI6IjAwNyIsImFnZSI6MTh9.Sq6L0ngQVCPPvtjW6GMYI9uQp7YsqbMDBN3mcVszSto";

        // 创建解析token对象
        JwtParser parser = Jwts.parser();

        // 设置签证
        parser.setSigningKey("itheima");

        // 获取解析后的内容
        Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        System.out.println("解析后的内容：" + claimsJws);

        Claims body = claimsJws.getBody();  // 载荷信息
        System.out.println(body);
    }



    @Test
    public void testCreateTokenForCustomPlayload(){
        // 1、获取创建jwt的对象
        JwtBuilder jwtBuilder = Jwts.builder();

        // 2、设置jwt的组成部分
        // 2.1 头部
        Map<String, Object> header = new HashMap<>();
        header.put("address", "广东深圳");
        header.put("school", "五道口职业技术学院");
        jwtBuilder.setHeader(header);
        // 2.2 载荷
//        jwtBuilder.setIssuer("tom");    // 签发者
//        jwtBuilder.setId("001");        // 唯一标识
//        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + 20000)); // 设置20s后过期

        // 自定义载荷
        Map<String, Object> map = new HashMap<>();
        map.put("id", "007");
        map.put("name", "tom");
        map.put("age", 18);
        map.put("role", "role_admin");
        jwtBuilder.setClaims(map);

        // 2.3 签证
        jwtBuilder.signWith(SignatureAlgorithm.HS256, "itheima");

        // 3、生成token
        String jwt = jwtBuilder.compact();
        System.out.println("token:" + jwt);

    }
}
