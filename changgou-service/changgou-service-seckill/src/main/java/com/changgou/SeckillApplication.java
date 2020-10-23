package com.changgou;

import entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName SeckillApplication
 * @Description
 * @Author 传智播客
 * @Date 9:19 2020/9/21
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@MapperScan(basePackages = {"com.changgou.seckill.dao"})
@EnableAsync   // 开启异步请求
public class SeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1, 1);
    }
}
