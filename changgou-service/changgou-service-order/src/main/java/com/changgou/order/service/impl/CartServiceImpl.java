package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description
 * @Author 传智播客
 * @Date 11:12 2020/9/17
 * @Version 2.1
 **/
@Service
public class CartServiceImpl implements CartService {


    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @author 栗子
     * @Description 商品加入购物车
     * @Date 11:15 2020/9/17
     * @param skuId         库存ID
     * @param num           购买数量
     * @param username      当前用户
     * @return void
     **/
    @Override
    public void add(Long skuId, Integer num, String username) {
        // 1、构建购物车对象并且封装数据
        OrderItem orderItem = getOrderItemBySkuId(skuId, num);
        // 2、将购物车保存到redis中   如果是同款商品（sku_id一样）：合并购买数量
        Boolean aBoolean = redisTemplate.boundHashOps("cart_" + username).hasKey(skuId);
        if (aBoolean){
            // 有同款商品，合并数量
            OrderItem oldOrderItem = (OrderItem) redisTemplate.boundHashOps("cart_" + username).get(skuId);
            oldOrderItem.setNum(oldOrderItem.getNum() + num);   // 合并数量
            redisTemplate.boundHashOps("cart_" + username).put(skuId, oldOrderItem);
        }else {
            redisTemplate.boundHashOps("cart_" + username).put(skuId, orderItem);
        }
    }

    /**
     * @author 栗子
     * @Description 展示购物车列表数据
     * @Date 11:52 2020/9/17
     * @param username
     * @return java.util.List<com.changgou.order.pojo.OrderItem>
     **/
    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> list = redisTemplate.boundHashOps("cart_" + username).values();
        return list;
    }

    private OrderItem getOrderItemBySkuId(Long skuId, Integer num) {
        OrderItem orderItem = new OrderItem();
        Sku sku = skuFeign.findById(skuId).getData();
        orderItem.setCategoryId3(sku.getCategoryId());      // 商品分类ID
        orderItem.setSpuId(sku.getSpuId());                 // 商品ID
        orderItem.setSkuId(skuId);                          // 库存ID
        orderItem.setName(sku.getName());                   // 商品名称
        orderItem.setPrice(sku.getPrice());                 // 商品单价
        orderItem.setNum(num);                              // 购买数量
        orderItem.setMoney(sku.getPrice() * num);           // 总价
        orderItem.setPayMoney(sku.getPrice() * num - 0);    // 实付金额 = 总金额 - 优惠金额
        orderItem.setImage(sku.getImage());                 // 商品图片
        orderItem.setWeight(sku.getWeight());               // 商品重量
        orderItem.setPostFee(9);                            // 运费： 判断是否是超级会员   进一步判断：总金额是否大于99元
        orderItem.setIsReturn("0");                         // 退货状态：未退货
        return orderItem;
    }
}
