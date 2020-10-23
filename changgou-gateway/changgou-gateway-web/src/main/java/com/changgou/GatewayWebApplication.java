package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName GatewayWebApplication
 * @Description
 * @Author 传智播客
 * @Date 9:21 2020/9/14
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class, args);
    }

    // 定义的bean，存储客户端的IP
    @Bean(name = "ipKeyResolver")
    public KeyResolver keyResolver(){
        return new KeyResolver() {
            // flux编程
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                ServerHttpRequest request = exchange.getRequest();
                String ip = request.getRemoteAddress().getAddress().getHostAddress();
                return Mono.just(ip);
            }
        };
    }
}
