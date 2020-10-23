package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SeckillGoodsTask
 * @Description 将数据库数据写入Redis
 * @Author 传智播客
 * @Date 9:57 2020/9/21
 * @Version 2.1
 **/
@Component
public class SeckillGoodsTask {

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Scheduled(cron = "0/10 * * * * ?")
    public void getDbDataToRedis(){
        // 获取时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {

            // 将当前的时间段作为redis中的key
            String key_rule = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);

            // 查询每个时间段下的秒杀商品列表数据
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", 1);   // 审核通过后的商品
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu); // 大于开始时间
            criteria.andLessThanOrEqualTo("endTime", DateUtil.addDateHour(dateMenu, 2));  // 小于结束时间
            criteria.andGreaterThan("stockCount", 0);   // 库存量大于0
            // select * from tb_seckill_goods where status = 1 and stockCount > 0 and start_time > xxx and end_time < yyy  id not in(1, 2, 3)
            // 思路：redis中已有的id就不在查询了
            Set ids = redisTemplate.boundHashOps("SeckillGoods_" + key_rule).keys();
            if (ids != null && ids.size() > 0){
                criteria.andNotIn("id", ids);
            }

            List<SeckillGoods> list = seckillGoodsMapper.selectByExample(example);

//            System.out.println("查询的商品个数：" + list.size());

            // 将各个时间段数据写入Redis中
            if (list != null && list.size() > 0){
                for (SeckillGoods seckillGoods : list) {
                    // redis优化：减少客户端的交互次数
//                    Boolean aBoolean = redisTemplate.boundHashOps("SeckillGoods_" + key_rule).hasKey(seckillGoods.getId());

                    redisTemplate.boundHashOps("SeckillGoods_" + key_rule).put(seckillGoods.getId(), seckillGoods);

                    // 将商品对应的库存量存储到Redis中
                    redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGoods.getId(), seckillGoods.getStockCount());

                }
            }

        }
    }
}
