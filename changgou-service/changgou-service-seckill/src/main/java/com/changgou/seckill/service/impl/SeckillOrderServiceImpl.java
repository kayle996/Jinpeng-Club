package com.changgou.seckill.service.impl;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.thread.MultiThreadSubmitOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/****
 * @Author:传智播客
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired(required = false)
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MultiThreadSubmitOrder multiThreadSubmitOrder;

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * @author 栗子
     * @Description 支付成功，同步订单到数据库
     * @Date 11:56 2020/9/22
     * @param username      用户
     * @param out_trade_no  订单号
     * @param transaction_id 交易流水号
     * @param time_end      支付完成时间
     * @return void
     **/
    @Override
    public void updateStatus(String username, String out_trade_no, String transaction_id, String time_end) throws ParseException {
        // 获取订单
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        if (seckillStatus != null){
            SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("Order_" + username).get(seckillStatus.getGoodsId());
            if (seckillOrder != null){
                // 将订单同步到数据库
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  // 日期格式：微信提供的格式
                Date payTime = sdf.parse(time_end);
                seckillOrder.setPayTime(payTime);      // 支付完成时间
                seckillOrder.setStatus("1");    // 支付状态：支付成功
                seckillOrder.setTransactionId(transaction_id);

                seckillOrderMapper.insertSelective(seckillOrder);
            }

            // 删除订单
            redisTemplate.boundHashOps("Order_" + username).delete(seckillStatus.getGoodsId());
            // 删除用户重复下单信息(场景：秒杀   手机：特价商品 100件)
            redisTemplate.boundHashOps("UserQueueCount_" + username).delete(seckillStatus.getGoodsId());
            // 删除用户的订单信息
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
        }

    }

    /**
     * @author 栗子
     * @Description 支付失败，删除订单并且回滚库存
     * @Date 11:57 2020/9/22
     * @param username
     * @return void
     **/
    @Override
    public void deleteOrder(String username) {
        // 支付失败，将订单设置过期时间（30分钟）

        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        // 获取该秒杀的商品
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());

        // 删除订单
        redisTemplate.boundHashOps("Order_" + username).delete(seckillStatus.getGoodsId());

        // 回滚库存
        if (seckillGoods != null){
            // 回滚该商品
            Integer stockCount = seckillGoods.getStockCount();
            stockCount = stockCount + 1;
            seckillGoods.setStockCount(stockCount);
            redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillStatus.getGoodsId(), seckillGoods);
            // 回滚该商品的库存量
            redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), 1);
        }else {
            // 如果只剩下一件：下单完成后，将该商品从redis移除了
            // 将该商品从新放回Redis
            SeckillGoods seckillGoodsByDb = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
            redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillStatus.getGoodsId(), seckillGoodsByDb);
            redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), 1);
        }

        // 删除用户重复下单信息(场景：秒杀   手机：特价商品 100件)
        redisTemplate.boundHashOps("UserQueueCount_" + username).delete(seckillStatus.getGoodsId());
        // 删除用户的订单信息
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }

    /**
     * @author 栗子
     * @Description 提交订单
     * @Date 11:26 2020/9/21
     * @param time
     * @param seckillId 秒杀商品ID   限制一件
     * @param userId    用户名
     * @return java.lang.Boolean
     **/
    @Override
    public Boolean add(String time, Long seckillId, String userId) {
        // 通过计数器：防止用户对同款商品进行重复下单
        Long increment = redisTemplate.boundHashOps("UserQueueCount_" + userId).increment(seckillId, 1);
        if (increment > 1){
            throw new RuntimeException("对不起，不允许对同款商品进行重复下单");
        }


        // 将用户的排队信息封装到Redis的队列中
        SeckillStatus seckillStatus = new SeckillStatus(userId, new Date(), 1, seckillId, time);
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);

        // 存储用户订单状态信息
        redisTemplate.boundHashOps("UserQueueStatus").put(userId, seckillStatus);
//        redisTemplate.boundHashOps("UserQueueStatus_" + userId).put(seckillId, seckillStatus);

        // 多线程下单
        Future<Boolean> future = multiThreadSubmitOrder.createOrder();
        // 获取异步执行的结果
        try {
            Boolean aBoolean = future.get();
            return aBoolean;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * SeckillOrder条件+分页查询
     * @param seckillOrder 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder){
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder){
        Example example=new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(seckillOrder!=null){
            // 主键
            if(!StringUtils.isEmpty(seckillOrder.getId())){
                    criteria.andEqualTo("id",seckillOrder.getId());
            }
            // 秒杀商品ID
            if(!StringUtils.isEmpty(seckillOrder.getSeckillId())){
                    criteria.andEqualTo("seckillId",seckillOrder.getSeckillId());
            }
            // 支付金额
            if(!StringUtils.isEmpty(seckillOrder.getMoney())){
                    criteria.andEqualTo("money",seckillOrder.getMoney());
            }
            // 用户
            if(!StringUtils.isEmpty(seckillOrder.getUserId())){
                    criteria.andEqualTo("userId",seckillOrder.getUserId());
            }
            // 创建时间
            if(!StringUtils.isEmpty(seckillOrder.getCreateTime())){
                    criteria.andEqualTo("createTime",seckillOrder.getCreateTime());
            }
            // 支付时间
            if(!StringUtils.isEmpty(seckillOrder.getPayTime())){
                    criteria.andEqualTo("payTime",seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if(!StringUtils.isEmpty(seckillOrder.getStatus())){
                    criteria.andEqualTo("status",seckillOrder.getStatus());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(seckillOrder.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if(!StringUtils.isEmpty(seckillOrder.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",seckillOrder.getReceiverMobile());
            }
            // 收货人
            if(!StringUtils.isEmpty(seckillOrder.getReceiver())){
                    criteria.andEqualTo("receiver",seckillOrder.getReceiver());
            }
            // 交易流水
            if(!StringUtils.isEmpty(seckillOrder.getTransactionId())){
                    criteria.andEqualTo("transactionId",seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder){
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder){
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id){
        return  seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }

    /**
     * @author 栗子
     * @Description 查询订单状态
     * @Date 12:28 2020/9/21
     * @param userId
     * @return com.changgou.seckill.pojo.SeckillStatus
     **/
    @Override
    public SeckillStatus queryStatus(String userId) {
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(userId);
        return seckillStatus;
    }
}
