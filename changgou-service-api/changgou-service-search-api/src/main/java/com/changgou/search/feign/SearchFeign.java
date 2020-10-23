package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName SearchFeign
 * @Description
 * @Author 传智播客
 * @Date 10:53 2020/9/12
 * @Version 2.1
 **/
@FeignClient(name = "search")
@RequestMapping("/search")
public interface SearchFeign {

    /**
     * @author 栗子
     * @Description 搜索接口
     * @Date 10:53 2020/9/12
     * @param searchMap
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    @GetMapping
    Map<String, Object> search(@RequestParam(required = false) Map<String, String> searchMap);
}
