package com.changgou.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName EnableMvcConfig
 * @Description
 * @Author 传智播客
 * @Date 16:27 2020/9/12
 * @Version 2.1
 **/
@Configuration
@ControllerAdvice
public class EnableMvcConfig implements WebMvcConfigurer{

    // 添加需要放行的资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/item/**")
//                .addResourceLocations("classpath:templates/item");
        // 对资源放行
        registry.addResourceHandler("/item/**")
                .addResourceLocations("classpath:/templates/item/");
    }
}
