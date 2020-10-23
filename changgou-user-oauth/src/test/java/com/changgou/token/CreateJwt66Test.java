package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CreateJwt97Test
 * @Description
 * @Author 传智播客
 * @Date 18:40 2019/8/21
 * @Version 2.1
 **/
public class CreateJwt66Test {

    /***
     * 创建令牌测试
     */
    @Test
    public void testCreateToken(){
        //证书文件路径
        String key_location="changgou97.jks";
        //秘钥库密码
        String key_password="changgou97";
        //秘钥密码
        String keypwd = "changgou97";
        //秘钥别名
        String alias = "changgou97";

        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);

        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());

        //读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();

        //定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "1");
        tokenMap.put("name", "itheima");
        tokenMap.put("roles", "ROLE_VIP,ROLE_USER");

        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivate));

        //取出令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }


    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.cENnZL48uzyrooTPIDQ22gLr3783PkN1QkXdjelYdFYjF5gI3pUihkL3JMtUlLv49KuROjWUndSGrVXVl3Ic1BUj3C6hJTO1BL4KKzIyod7ESHVm6B3oqzx8V6vahmfEbqmZKT7G3SkdClZJt1MmPgldjdj-ZQ82LUv80rarnTasinpD62Eb8cImfXphsYH7ZfCBjtE9xHo56q0D4mGnqYtYRG_PheuwW36tlxebTHWxQGHl83x-joEaSrNaITHVVFG1XJpEbTkR19Fqq55ym_vyrJrSbjLSH8h__PdGjDSQX465kxiM0vjLM0y_9MYNT5W8qzO8WV8hrUequ9zD_Q";
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5TBwqdGqp/6+KfxWT8x8rKOS/nojP7ikjUik0KhfpW8EVj+8ffo4sUxsJ0/Nl0dlcKHd+p7S/RWRwoRY9sK/6WE+2Xc1pHte4LoRDKimJDolHExpnlFEpA4uDPmgbqWN2monuWPMSqnHQny8oMq6yI7dcePtjCkLWCX5YtWrCzhYAdvXcoDD4CPy78nYeWQpmyQoblcpEW4fRWgXncvC1mbK/KJ8l9/9GD3w9zMHuZgMXuSZFDg+3Xi0DdI4gt5UNaNvLKvOTxWXmcI9sPqvYRWnYqaDpvwI1Dion8TjYVlNAPMjMRix0PWYGSKSXnjg6GDU3wGU2of4FYDDePxs/QIDAQAB-----END PUBLIC KEY-----";
        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

}
