package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName WeiXinPayController
 * @Description
 * @Author 传智播客
 * @Date 10:01 2020/9/20
 * @Version 2.1
 **/
@RestController
@RequestMapping("/weixin/pay")
public class WeiXinPayController {

    @Autowired
    private WeiXinPayService weiXinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment evn;


    @RequestMapping("/notify/url2")
    public void notifyUrl2(@RequestParam Map<String, String> map) throws Exception {
        // 在postman直接访问该方法
        // 将支付结果信息发送mq中  redis的计数器 incr   out_trande_no + 1
        String exchange = evn.getProperty("mq.pay.exchange.order");
        String routingKey = evn.getProperty("mq.pay.routing.key");
        rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(map));   // 坑：需要将map转成json
    }

    /**
     * @author 栗子
     * @Description 微信的回调
     * @Date 11:11 2020/9/20
     * @param
     * @return void
     **/
    @RequestMapping("/notify/url")
    public void notifyUrl(HttpServletRequest request) throws Exception {
//        System.out.println("微信通知成功了...");
        // 获取微信的通知结果数据：request,微信发送的请求（网络传输）
        ServletInputStream is = request.getInputStream();   // 微信通知的流数据
        // 将is转成字节数组
        ByteArrayOutputStream os = new ByteArrayOutputStream(); // 缓存流对象
        // 定义一个缓冲区
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1){
            os.write(buffer, 0, len);
        }
        os.flush();
        // 关闭流
        is.close();
        os.close();

        // 转成String
        String strXML = new String(os.toByteArray(), "UTF-8");
        // 获取微信的通知结果的数据
        Map<String, String> map = WXPayUtil.xmlToMap(strXML);
        System.out.println("获取到微信通知的结果：" + map);
        // 获取附加数据
        String attach = map.get("attach");
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);

        // 将支付结果信息发送mq中  redis的计数器 incr   out_trande_no + 1
//        String exchange = evn.getProperty("mq.pay.exchange.order");
//        String routingKey = evn.getProperty("mq.pay.routing.key");
        String exchange = evn.getProperty(attachMap.get("exchange"));
        String routingKey = evn.getProperty(attachMap.get("routingKey"));
        System.out.println("交换机：" + exchange);
        System.out.println("路由器：" + routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(map));   // 坑：需要将map转成json

    }

    /**
     * @author 栗子
     * @Description 创建支付链接
     * @Date 10:07 2020/9/20
     * @return entity.Result
     **/
    @RequestMapping("/create/native")
//    public Result createNative(String outtradeno, String money, String exchange, String routingKey){
    public Result createNative(@RequestParam Map<String, String> parameters){
//        Map<String, String> map = weiXinPayService.createNative(outtradeno, money);
        Map<String, String> map = weiXinPayService.createNative(parameters);
        return new Result(true, StatusCode.OK, "创建支付链接成功", map);
    }

    /**
     * @author 栗子
     * @Description 查询支付状态
     * @Date 10:49 2020/9/20
     * @param outtradeno
     * @return entity.Result
     **/
    @RequestMapping("/query/status")
    public Result queryStatus(String outtradeno){
        Map<String, String> map = weiXinPayService.queryStatus(outtradeno);
        return new Result(true, StatusCode.OK, "查询支付状态", map);
    }
}
