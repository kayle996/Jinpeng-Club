package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillGoods;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:SeckillGoods业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillGoodsService {

    /***
     * SeckillGoods多条件分页查询
     * @param seckillGoods
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillGoods> findPage(SeckillGoods seckillGoods, int page, int size);

    /***
     * SeckillGoods分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillGoods> findPage(int page, int size);

    /***
     * SeckillGoods多条件搜索方法
     * @param seckillGoods
     * @return
     */
    List<SeckillGoods> findList(SeckillGoods seckillGoods);

    /***
     * 删除SeckillGoods
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillGoods数据
     * @param seckillGoods
     */
    void update(SeckillGoods seckillGoods);

    /***
     * 新增SeckillGoods
     * @param seckillGoods
     */
    void add(SeckillGoods seckillGoods);

    /**
     * 根据ID查询SeckillGoods
     * @param id
     * @return
     */
     SeckillGoods findById(Long id);

    /***
     * 查询所有SeckillGoods
     * @return
     */
    List<SeckillGoods> findAll();

    List<SeckillGoods> list(String time);

    /**
     * @author 栗子
     * @Description 查看商品详情
     * @Date 11:02 2020/9/21
     * @param time
     * @param id
     * @return seckillGoods
     **/
    SeckillGoods one(String time, Long id);
}
