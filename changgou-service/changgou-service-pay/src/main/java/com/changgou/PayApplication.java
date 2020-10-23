package com.changgou;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @ClassName PayApplication
 * @Description
 * @Author 传智播客
 * @Date 9:47 2020/9/20
 * @Version 2.1
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }

    @Autowired
    private Environment evn;


    /***************************************普通订单队列-start***************************************/
    // 创建队列
    @Bean
    public Queue orderQueue(){
        return new Queue(evn.getProperty("mq.pay.queue.order"), true);
    }

    // 创建交换机
    @Bean
    public Exchange orderExchange(){
        return new DirectExchange(evn.getProperty("mq.pay.exchange.order"), true, false);
    }

    // 将队列绑定到交换机
    @Bean
    public Binding bindingQueueToExchangeByOrder(Queue orderQueue, Exchange orderExchange){
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(evn.getProperty("mq.pay.routing.key")).noargs();
    }
    /***************************************普通订单队列-end***************************************/


    /***************************************秒杀订单队列-start***************************************/
    // 创建队列
    @Bean
    public Queue seckillOrderQueue(){
        return new Queue(evn.getProperty("mq.pay.queue.seckillorder"), true);
    }

    // 创建交换机
    @Bean
    public Exchange seckillOrderExchange(){
        return new DirectExchange(evn.getProperty("mq.pay.exchange.seckillorder"), true, false);
    }

    // 将队列绑定到交换机
    @Bean
    public Binding bindingQueueToExchangeBySeckillOrder(Queue seckillOrderQueue, Exchange seckillOrderExchange){
        return BindingBuilder.bind(seckillOrderQueue).to(seckillOrderExchange).with(evn.getProperty("mq.pay.routing.seckillkey")).noargs();
    }
    /***************************************秒杀订单队列-end***************************************/
}
