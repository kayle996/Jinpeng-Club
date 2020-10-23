package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService {

    /**
     * @author 栗子
     * @Description 保存商品
     * @Date 11:48 2020/9/6
     * @param goods
     * @return void
     **/
    void saveOrUpdate(Goods goods);

    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
     Spu findById(Long id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();


    Goods findGoodsById(Long id);

    /**
     * @author 栗子
     * @Description 商品审核
     * @Date 14:46 2020/9/6
     * @param id
     * @param status
     * @return void
     **/
    void audit(Long id, String status);

    /**
     * @author 栗子
     * @Description 商品的上架或者下架操作
     * @Date 15:04 2020/9/6
     * @param id
     * @param isMarketable
     * @return void
     **/
    void isShow(Long id, String isMarketable);

    void isDel(Long id, String isDelete);
}
