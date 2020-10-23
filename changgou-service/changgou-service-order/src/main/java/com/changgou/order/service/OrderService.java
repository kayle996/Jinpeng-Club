package com.changgou.order.service;

import com.changgou.order.pojo.Order;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:Order业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface OrderService {

    /**
     * @author 栗子
     * @Description 支付成功，更新订单
     * @Date 12:14 2020/9/20
     * @param out_trade_no
     * @param transaction_id
     * @return void
     **/
    void updateStatus(String out_trade_no, String transaction_id);

    /**
     * @author 栗子
     * @Description 支付失败，删除订单
     * @Date 12:15 2020/9/20
     * @param out_trade_no
     * @return void
     **/
    void deleteOrder(String out_trade_no);

    /***
     * 新增Order
     * @param order
     */
    Order add(Order order);

    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(Order order, int page, int size);

    /***
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(int page, int size);

    /***
     * Order多条件搜索方法
     * @param order
     * @return
     */
    List<Order> findList(Order order);

    /***
     * 删除Order
     * @param id
     */
    void delete(String id);

    /***
     * 修改Order数据
     * @param order
     */
    void update(Order order);



    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
     Order findById(String id);

    /***
     * 查询所有Order
     * @return
     */
    List<Order> findAll();

}
