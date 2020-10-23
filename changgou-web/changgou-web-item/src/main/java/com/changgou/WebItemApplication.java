package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName WebItemApplication
 * @Description
 * @Author 传智播客
 * @Date 15:13 2020/9/12
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.goods.feign"})
public class WebItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebItemApplication.class, args);
    }
}
