package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:传智播客
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired(required = false)
    private UserFeign userFeign;

    /**
     * @author 栗子
     * @Description 支付成功，更新订单
     * @Date 12:14 2020/9/20
     * @param out_trade_no
     * @param transaction_id
     * @return void
     **/
    @Override
    public void updateStatus(String out_trade_no, String transaction_id) {
        // 从redis查询订单
        Order order = (Order) redisTemplate.boundHashOps("order").get(out_trade_no);
        if (order == null){
            // 如果redis没有，则从数据库中查询
            order = orderMapper.selectByPrimaryKey(out_trade_no);
        }

        if (order != null){
            // 再更新订单信息
            order.setUpdateTime(new Date());        // 订单更新时间
            order.setPayTime(new Date());           // 订单支付时间
            order.setPayStatus("1");                // 支付状态：成功
            order.setTransactionId(transaction_id); // 交易流水号
            orderMapper.updateByPrimaryKeySelective(order);
        }

        // 删除redis中订单
        redisTemplate.boundHashOps("order").delete(out_trade_no);
    }


    /**
     * @author 栗子
     * @Description 支付失败，删除(更新)订单
     * @Date 12:15 2020/9/20
     * @param out_trade_no
     * @return void
     **/
    @Override
    public void deleteOrder(String out_trade_no) {
        // 从redis查询订单
        Order order = (Order) redisTemplate.boundHashOps("order").get(out_trade_no);
        if (order == null){
            // 如果redis没有，则从数据库中查询
            order = orderMapper.selectByPrimaryKey(out_trade_no);
        }

        if (order != null){
            // 再更新订单信息
            order.setUpdateTime(new Date());        // 订单更新时间
            order.setPayStatus("2");                // 支付状态：成功
            order.setIsDelete("1");                 // 订单删除
            orderMapper.updateByPrimaryKeySelective(order);
        }


        // 删除redis中订单
        redisTemplate.boundHashOps("order").delete(out_trade_no);
    }


    /**
     * 增加Order
     * @param order
     */
    @GlobalTransactional
    @Override
    public Order add(Order order){
        // 1、保存订单
        long orderId = idWorker.nextId();
        order.setId(String.valueOf(orderId));       // 设置主键
        // 获取购物车列表数据
        List<OrderItem> orderItems = cartService.list(order.getUsername());
        Integer totalNum = 0;       // 总数量
        Integer totalMoney = 0;     // 总金额
        Integer preMoney = 0;       // 优惠总金额
        for (OrderItem orderItem : orderItems) {
            totalNum += orderItem.getNum();
            totalMoney += orderItem.getMoney();
            preMoney += totalMoney - orderItem.getPayMoney();
        }
        order.setPostFee(9);      // 油费
        order.setCreateTime(new Date());        // 订单创建日期
        order.setUpdateTime(new Date());        // 订单更新时间
        order.setSourceType("1");               // 订单来源
        order.setOrderStatus("0");              // 订单状态
        order.setPayStatus("0");                // 订单支付状态
        order.setConsignStatus("0");            // 订单发货状态
        order.setIsDelete("0");                 // 订单删除状态
        orderMapper.insertSelective(order);

        // 2、保存订单明细(购物车列表)
        for (OrderItem orderItem : orderItems) {
            long id = idWorker.nextId();
            orderItem.setId(String.valueOf(id));    // 订单明细主键
            orderItem.setOrderId(order.getId());    // 订单ID：外键

            orderItemMapper.insertSelective(orderItem);

            // 细一点：哪个商品下单了，才删除Redis中的该商品
//            redisTemplate.boundHashOps("cart_" + order.getUsername()).delete(orderItem.getSkuId());
        }

        // 扣减库存
        skuFeign.decr(order.getUsername());

        // 增加用户积分
        userFeign.incr(order.getUsername(), "10");

        //int i = 9/0;    // 模拟错误

        // 保存订单到redis中
        redisTemplate.boundHashOps("order").put(order.getId(), order);

        // 3、下单成功后，删除购物车
        redisTemplate.delete("cart_" + order.getUsername());    // 购物车中的商品全部下单

        return order;
    }


    /**
     * Order条件+分页查询
     * @param order 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Order> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order){
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     * @param order
     * @return
     */
    public Example createExample(Order order){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(order!=null){
            // 订单id
            if(!StringUtils.isEmpty(order.getId())){
                    criteria.andEqualTo("id",order.getId());
            }
            // 数量合计
            if(!StringUtils.isEmpty(order.getTotalNum())){
                    criteria.andEqualTo("totalNum",order.getTotalNum());
            }
            // 金额合计
            if(!StringUtils.isEmpty(order.getTotalMoney())){
                    criteria.andEqualTo("totalMoney",order.getTotalMoney());
            }
            // 优惠金额
            if(!StringUtils.isEmpty(order.getPreMoney())){
                    criteria.andEqualTo("preMoney",order.getPreMoney());
            }
            // 邮费
            if(!StringUtils.isEmpty(order.getPostFee())){
                    criteria.andEqualTo("postFee",order.getPostFee());
            }
            // 实付金额
            if(!StringUtils.isEmpty(order.getPayMoney())){
                    criteria.andEqualTo("payMoney",order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if(!StringUtils.isEmpty(order.getPayType())){
                    criteria.andEqualTo("payType",order.getPayType());
            }
            // 订单创建时间
            if(!StringUtils.isEmpty(order.getCreateTime())){
                    criteria.andEqualTo("createTime",order.getCreateTime());
            }
            // 订单更新时间
            if(!StringUtils.isEmpty(order.getUpdateTime())){
                    criteria.andEqualTo("updateTime",order.getUpdateTime());
            }
            // 付款时间
            if(!StringUtils.isEmpty(order.getPayTime())){
                    criteria.andEqualTo("payTime",order.getPayTime());
            }
            // 发货时间
            if(!StringUtils.isEmpty(order.getConsignTime())){
                    criteria.andEqualTo("consignTime",order.getConsignTime());
            }
            // 交易完成时间
            if(!StringUtils.isEmpty(order.getEndTime())){
                    criteria.andEqualTo("endTime",order.getEndTime());
            }
            // 交易关闭时间
            if(!StringUtils.isEmpty(order.getCloseTime())){
                    criteria.andEqualTo("closeTime",order.getCloseTime());
            }
            // 物流名称
            if(!StringUtils.isEmpty(order.getShippingName())){
                    criteria.andEqualTo("shippingName",order.getShippingName());
            }
            // 物流单号
            if(!StringUtils.isEmpty(order.getShippingCode())){
                    criteria.andEqualTo("shippingCode",order.getShippingCode());
            }
            // 用户名称
            if(!StringUtils.isEmpty(order.getUsername())){
                    criteria.andLike("username","%"+order.getUsername()+"%");
            }
            // 买家留言
            if(!StringUtils.isEmpty(order.getBuyerMessage())){
                    criteria.andEqualTo("buyerMessage",order.getBuyerMessage());
            }
            // 是否评价
            if(!StringUtils.isEmpty(order.getBuyerRate())){
                    criteria.andEqualTo("buyerRate",order.getBuyerRate());
            }
            // 收货人
            if(!StringUtils.isEmpty(order.getReceiverContact())){
                    criteria.andEqualTo("receiverContact",order.getReceiverContact());
            }
            // 收货人手机
            if(!StringUtils.isEmpty(order.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",order.getReceiverMobile());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(order.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(!StringUtils.isEmpty(order.getSourceType())){
                    criteria.andEqualTo("sourceType",order.getSourceType());
            }
            // 交易流水号
            if(!StringUtils.isEmpty(order.getTransactionId())){
                    criteria.andEqualTo("transactionId",order.getTransactionId());
            }
            // 订单状态,0:未完成,1:已完成，2：已退货
            if(!StringUtils.isEmpty(order.getOrderStatus())){
                    criteria.andEqualTo("orderStatus",order.getOrderStatus());
            }
            // 支付状态,0:未支付，1：已支付，2：支付失败
            if(!StringUtils.isEmpty(order.getPayStatus())){
                    criteria.andEqualTo("payStatus",order.getPayStatus());
            }
            // 发货状态,0:未发货，1：已发货，2：已收货
            if(!StringUtils.isEmpty(order.getConsignStatus())){
                    criteria.andEqualTo("consignStatus",order.getConsignStatus());
            }
            // 是否删除
            if(!StringUtils.isEmpty(order.getIsDelete())){
                    criteria.andEqualTo("isDelete",order.getIsDelete());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }



    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }
}
