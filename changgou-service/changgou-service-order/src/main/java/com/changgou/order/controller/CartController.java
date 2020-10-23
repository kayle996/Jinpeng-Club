package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CartController
 * @Description
 * @Author 传智播客
 * @Date 11:12 2020/9/17
 * @Version 2.1
 **/
@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    /**
     * @author 栗子
     * @Description 商品加入购物车
     * @Date 11:15 2020/9/17
     * @param skuId
     * @param num
     * @return entity.Result
     **/
    @RequestMapping("/add")
    public Result add(Long skuId, Integer num){
        String username = TokenDecode.getUserInfo().get("username");
        cartService.add(skuId, num, username);
        return new Result(true, StatusCode.OK, "商品加入购物车成功");
    }

    /**
     * @author 栗子
     * @Description 展示购物车列表数据
     * @Date 11:51 2020/9/17
     * @param
     * @return entity.Result
     **/
    @RequestMapping("/list")
    public Result list(){
        String username = TokenDecode.getUserInfo().get("username");
        List<OrderItem> list = cartService.list(username);
        return new Result(true, StatusCode.OK, "购物车列表查询成功", list);
    }

}
