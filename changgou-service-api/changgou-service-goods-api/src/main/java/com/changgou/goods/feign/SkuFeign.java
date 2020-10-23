package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName SkuFeign
 * @Description
 * @Author 传智播客
 * @Date 11:14 2020/9/9
 * @Version 2.1
 **/
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /**
     * @author 栗子
     * @Description 扣减该用户购买商品的库存
     * @Date 11:51 2020/9/18
     * @param username
     * @return entity.Result
     **/
    @GetMapping("/decr/{username}")
    Result decr(@PathVariable(value = "username") String username);

    /**
     * @author 栗子
     * @Description 根据主键查询
     * @Date 11:19 2020/9/17
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Sku>
     **/
    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable(value = "id") Long id);

    /**
     * @author 栗子
     * @Description 通过spu_id获取库存列表数据
     * @Date 15:41 2020/9/12
     * @param spuId
     * @return entity.Result<java.util.List<com.changgou.goods.pojo.Sku>>
     **/
    @GetMapping("/findSkusbySpuId/{spuId}")
    Result<List<Sku>> findSkusbySpuId(@PathVariable(value = "spuId") Long spuId);

    /**
     * @author 栗子
     * @Description 查询正常状态下的库存列表数据
     * @Date 11:16 2020/9/9
     * @param status
     * @return java.util.List<com.changgou.goods.pojo.Sku>
     **/
    @RequestMapping("/findSkuListByStatus/{status}")
    List<Sku> findSkuListByStatus(@PathVariable(value = "status") String status);
}
