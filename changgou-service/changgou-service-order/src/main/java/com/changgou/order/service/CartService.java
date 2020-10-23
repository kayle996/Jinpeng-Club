package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * @ClassName CartService
 * @Description
 * @Author 传智播客
 * @Date 11:12 2020/9/17
 * @Version 2.1
 **/
public interface CartService {

    /**
     * @author 栗子
     * @Description 商品加入购物车
     * @Date 11:15 2020/9/17
     * @param skuId         库存ID
     * @param num           购买数量
     * @param username      当前用户
     * @return void
     **/
    void add(Long skuId, Integer num, String username);

    /**
     * @author 栗子
     * @Description 展示购物车列表数据
     * @Date 11:52 2020/9/17
     * @param username
     * @return java.util.List<com.changgou.order.pojo.OrderItem>
     **/
    List<OrderItem> list(String username);
}
