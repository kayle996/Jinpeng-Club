package com.changgou.goods.service;

import com.changgou.goods.pojo.Sku;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:Sku业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SkuService {

    /***
     * Sku多条件分页查询
     * @param sku
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(Sku sku, int page, int size);

    /***
     * Sku分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(int page, int size);

    /***
     * Sku多条件搜索方法
     * @param sku
     * @return
     */
    List<Sku> findList(Sku sku);

    /***
     * 删除Sku
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Sku数据
     * @param sku
     */
    void update(Sku sku);

    /***
     * 新增Sku
     * @param sku
     */
    void add(Sku sku);

    /**
     * 根据ID查询Sku
     * @param id
     * @return
     */
     Sku findById(Long id);

    /***
     * 查询所有Sku
     * @return
     */
    List<Sku> findAll();

    /**
     * @author 栗子
     * @Description 查询正常状态下的库存列表数据
     * @Date 11:18 2020/9/9
     * @param status
     * @return java.util.List<com.changgou.goods.pojo.Sku>
     **/
    List<Sku> findSkuListByStatus(String status);

    List<Sku> findSkusbySpuId(Long spuId);

    /**
     * @author 栗子
     * @Description 扣减该用户购买商品的库存
     * @Date 11:52 2020/9/18
     * @param username
     * @return void
     **/
    void decr(String username);
}
