package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

/**
 * @ClassName SeckillOrderListener
 * @Description 秒杀订单的监听器
 * @Author 传智播客
 * @Date 11:34 2020/9/22
 * @Version 2.1
 **/
@Component
public class SeckillOrderListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * @author 栗子
     * @Description 监听秒杀队列并且消费消息
     * @Date 11:35 2020/9/22
     * @param text
     * @return void
     **/
    @RabbitListener(queues = {"${mq.pay.queue.seckillorder}"})
    public void readMsg(String text) throws ParseException {
        // 将消息转成map
        Map<String, String> map = JSON.parseObject(text, Map.class);
        // 获取附加数据
        String attach = map.get("attach");
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);
        String username = attachMap.get("username");
        // 消费消息
        String return_code = map.get("return_code");    // 通信标识
        if ("SUCCESS".equals(return_code)){
            String result_code = map.get("result_code");    // 交易标识
            if ("SUCCESS".equals(result_code)){
                String out_trade_no = map.get("out_trade_no");
                String transaction_id = map.get("transaction_id");
                String time_end = map.get("time_end");
                // 支付成功，将缓存中的订单同步到数据
                seckillOrderService.updateStatus(username, out_trade_no, transaction_id, time_end);
            }else {
                // 支付失败，删除订单
                seckillOrderService.deleteOrder(username);
            }
        }
    }
}
