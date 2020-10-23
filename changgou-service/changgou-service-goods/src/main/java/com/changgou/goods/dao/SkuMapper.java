package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:传智播客
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {


    /**
     * @author 栗子
     * @Description 扣减该用户购买商品的库存
     * @Date 11:57 2020/9/18
     * @param skuId
     * @param num   购买数量
     * @return int
     **/
    @Update("UPDATE tb_sku SET num = num - #{num} WHERE id = #{skuId} AND num > #{num}")
    int decr(@Param("skuId") Long skuId, @Param("num") Integer num);
}
