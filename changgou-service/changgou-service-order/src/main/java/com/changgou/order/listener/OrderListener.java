package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName OrderListener
 * @Description 订单监听器
 * @Author 传智播客
 * @Date 12:05 2020/9/20
 * @Version 2.1
 **/
@Component
public class OrderListener {

    @Autowired
    private OrderService orderService;

    /**
     * @author 栗子
     * @Description 消息监听器
     * @Date 12:06 2020/9/20
     * @param text
     * @return void
     **/
    @RabbitListener(queues = {"${mq.pay.queue.order}"})
    public void readMsg(String text){
        // 获取消息（json）
        Map<String, String> map = JSON.parseObject(text, Map.class);
        // 消费消息
        if (map != null){
            String return_code = map.get("return_code");    // 通信标识
            if ("SUCCESS".equals(return_code)){
                String result_code = map.get("result_code");    // 交易标识
                String out_trade_no = map.get("out_trade_no");  // 订单号
                if ("SUCCESS".equals(result_code)){
                    String transaction_id = map.get("transaction_id");
                    // 支付成功：更新订单
                    orderService.updateStatus(out_trade_no, transaction_id);
                }else {
                    // 支付失败：删除订单
                    orderService.deleteOrder(out_trade_no);
                }
            }
        }

    }
}
