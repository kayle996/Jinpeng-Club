package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName WebSearchApplication
 * @Description
 * @Author 传智播客
 * @Date 10:19 2020/9/12
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.changgou.search.feign")
public class WebSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSearchApplication.class, args);
    }
}
