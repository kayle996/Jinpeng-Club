package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {


    /**
     * @author 栗子
     * @Description 加载商品的品牌列表
     * @Date 10:21 2020/9/6
     * @param categoryId
     * @return java.util.List<com.changgou.goods.pojo.Brand>
     **/
    @Select("SELECT * FROM tb_brand tb, tb_category_brand tcb WHERE tb.id = tcb.brand_id AND tcb.category_id = #{categoryId}")
    List<Brand> findBrandListByCategoryId(Integer categoryId);
}
