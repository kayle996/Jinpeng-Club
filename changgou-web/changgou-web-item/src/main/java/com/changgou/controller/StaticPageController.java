package com.changgou.controller;

import com.changgou.service.StaticPageService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName StaticPageController
 * @Description 生成静态页面
 * @Author 传智播客
 * @Date 15:45 2020/9/12
 * @Version 2.1
 **/
@RestController
@RequestMapping("/page")
public class StaticPageController {

    @Autowired
    private StaticPageService staticPageService;

    /**
     * @author 栗子
     * @Description 生成静态页面
     * @Date 15:48 2020/9/12
     * @param id
     * @return entity.Result
     **/
    @RequestMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(value = "id") Long id){
        staticPageService.index(id);
        return new Result(true, StatusCode.OK, "生成静态页成功");
    }

}
