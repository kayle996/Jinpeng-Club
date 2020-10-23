package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.github.pagehelper.PageInfo;

import java.text.ParseException;
import java.util.List;

/****
 * @Author:传智播客
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService {

    /**
     * @author 栗子
     * @Description 支付成功，同步订单到数据库
     * @Date 11:56 2020/9/22
     * @param username      用户
     * @param out_trade_no  订单号
     * @param transaction_id 交易流水号
     * @param time_end      支付完成时间
     * @return void
     **/
    void updateStatus(String username, String out_trade_no, String transaction_id, String time_end) throws ParseException;

    /**
     * @author 栗子
     * @Description 支付失败，删除订单
     * @Date 11:57 2020/9/22
     * @param username
     * @return void
     **/
    void deleteOrder(String username);


    /**
     * @author 栗子
     * @Description 用户提交订单
     * @Date 11:19 2020/9/21
     * @param time
     * @param id
     * @param userId
     * @return void
     **/
    Boolean add(String time, Long id, String userId);


    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /***
     * 新增SeckillOrder
     * @param seckillOrder
     */
    void add(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
     SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();


    /**
     * @author 栗子
     * @Description 查询订单状态
     * @Date 12:28 2020/9/21
     * @param userId
     * @return com.changgou.seckill.pojo.SeckillStatus
     **/
    SeckillStatus queryStatus(String userId);


}
