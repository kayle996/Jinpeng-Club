package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName UserFeign
 * @Description
 * @Author 传智播客
 * @Date 10:17 2020/9/17
 * @Version 2.1
 **/
@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {

    /**
     * @author 栗子
     * @Description 增加当前用户的积分
     * @Date 12:01 2020/9/18
     * @param username
     * @param points
     * @return entity.Result
     **/
    @GetMapping("/incr/{username}/{points}")
    Result incr(@PathVariable(value = "username") String username, @PathVariable(value = "points") String points);

    /**
     * @author 栗子
     * @Description 根据username查询用户信息
     * @Date 10:18 2020/9/17
     * @param id
     * @return entity.Result<com.changgou.user.pojo.User>
     **/
    @GetMapping("/{id}")
    Result<User> findById(@PathVariable(value = "id") String id);
}
