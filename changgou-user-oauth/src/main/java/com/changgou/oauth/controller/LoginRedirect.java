package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName LoginRedirect
 * @Description 登录页面的跳转
 * @Author 传智播客
 * @Date 9:15 2020/9/18
 * @Version 2.1
 **/
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {

    /**
     * @author 栗子
     * @Description
     * @Date 9:16 2020/9/18
     * @param
     * @return java.lang.String
     **/
    @RequestMapping("/login")
    public String login(@RequestParam(value = "ReturnUrl", required = false) String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "login";
    }
}
