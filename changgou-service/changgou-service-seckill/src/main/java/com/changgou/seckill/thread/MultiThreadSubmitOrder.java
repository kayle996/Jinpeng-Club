package com.changgou.seckill.thread;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * @ClassName MultiThreadSubmitOrder
 * @Description
 * @Author 传智播客
 * @Date 11:59 2020/9/21
 * @Version 2.1
 **/
@Component
public class MultiThreadSubmitOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Async
    public Future<Boolean> createOrder(){
        try {
            // 用户的下单的排队信息从redis的队列中获取
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
            String time = seckillStatus.getTime();
            Long seckillId = seckillStatus.getGoodsId();
            String userId = seckillStatus.getUsername();

            // 获取秒杀的商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(seckillId);
            if (seckillGoods == null){
                throw new RuntimeException("该商品已售罄...");
            }

            // 提交订单（将订单对象存到Redis）
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());              // 主键
            seckillOrder.setSeckillId(seckillId);               // 秒杀商品ID
            seckillOrder.setMoney(seckillGoods.getCostPrice()); // 秒杀价
            seckillOrder.setCreateTime(new Date());             // 订单创建日期
            seckillOrder.setStatus("0");                        // 支付状态：未支付
            redisTemplate.boundHashOps("Order_" + userId).put(seckillId, seckillOrder);

            // 售卖一件后，所剩的库存量
//            Integer stockCount = seckillGoods.getStockCount() - 1;  // 商品的库存量
            // 现在扣减库存：从Redis中减
            Long stockCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillId, -1);
            seckillGoods.setStockCount(stockCount.intValue()); // 更新库存
            if (stockCount == 0){
                // 最后一件商品已卖完
                // 移除Redis中的商品
                redisTemplate.boundHashOps("SeckillGoods_" + time).delete(seckillId);
                // 同步数据库
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            }else if (stockCount < 0){
                // 删除用户重复下单信息
                redisTemplate.boundHashOps("UserQueueCount_" + userId).delete(seckillId);
                // 删除用户的订单信息
                redisTemplate.boundHashOps("UserQueueStatus").delete(userId);
                throw new RuntimeException("对不起，该商品已售罄。。。");
            }else {
                // 没有超卖，更该商品新库存量
                redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillId, seckillGoods);
            }

//            if (stockCount >= 0){
//                // redis的库存就需要更新
//                redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillId, seckillGoods);
//            }else {
//                // 移除Redis中的商品
//                redisTemplate.boundHashOps("SeckillGoods_" + time).delete(seckillId);
//                // 同步数据库
//                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
//            }

            // 下单成功，更新订单状态
            seckillStatus.setStatus(2);     // 订单状态信息
            seckillStatus.setOrderId(seckillOrder.getId()); // 订单ID
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney())); // 订单价格
            redisTemplate.boundHashOps("UserQueueStatus").put(userId, seckillStatus);
            return new AsyncResult<>(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("方法异常了");
            return new AsyncResult<>(false);
        }
    }
}
