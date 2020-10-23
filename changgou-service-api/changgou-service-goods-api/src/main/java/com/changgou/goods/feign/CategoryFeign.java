package com.changgou.goods.feign;

import com.changgou.goods.pojo.Category;
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
@RequestMapping("/category")
public interface CategoryFeign {

    /**
     * @author 栗子
     * @Description 获取商品的分类
     * @Date 15:39 2020/9/12
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Category>
     **/
    @GetMapping("/{id}")
    Result<Category> findById(@PathVariable(value = "id") Integer id);
}
