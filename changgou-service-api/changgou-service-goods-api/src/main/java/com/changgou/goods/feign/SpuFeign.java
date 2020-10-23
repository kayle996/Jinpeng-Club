package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
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
@RequestMapping("/spu")
public interface SpuFeign {

    /**
     * @author 栗子
     * @Description 通过主键查询
     * @Date 15:43 2020/9/12
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Spu>
     **/
    @GetMapping("/{id}")
    Result<Spu> findById(@PathVariable(value = "id") Long id);
}
