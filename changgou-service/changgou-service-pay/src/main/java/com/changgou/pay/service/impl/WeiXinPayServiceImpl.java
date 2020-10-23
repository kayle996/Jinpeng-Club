package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeiXinPayServiceImpl
 * @Description
 * @Author 传智播客
 * @Date 10:06 2020/9/20
 * @Version 2.1
 **/
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Value("${weixin.appid}")
    private String appid;           // 微信公众账号或开放平台APP的唯一标识

    @Value("${weixin.partner}")
    private String partner;         // 财付通平台的商户账号

    @Value("${weixin.partnerkey}")
    private String partnerkey;      // 财付通平台的商户密钥

    @Value("${weixin.notifyurl}")
    private String notifyurl;       // 回调地址


    /**
     * @author 栗子
     * @Description 创建支付链接
     * @Date 10:06 2020/9/20
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    @Override
//    public Map<String, String> createNative(String out_trade_no, String total_fee) {
    public Map<String, String> createNative(Map<String, String> parameters) {

        try {
            // 调用统一下单接口地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

            // 封装请求参数（map）
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);               // 公众账号
            data.put("mch_id", partner);            // 商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());//        随机字符串
            data.put("body", "畅购97商城-订单支付");  // 商品描述
            data.put("out_trade_no", parameters.get("out_trade_no")); // 商户订单号
            data.put("total_fee", parameters.get("total_fee"));       // 标价金额
            data.put("spbill_create_ip", "127.0.0.1");  // 终端IP
            data.put("notify_url", notifyurl);          // 通知地址
            data.put("trade_type", "NATIVE");           // 交易类型

            // 提供附加数据（额外数据）
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("exchange", parameters.get("exchange"));      // 交换机
            attachMap.put("routingKey", parameters.get("routingKey"));  // 路由器
            attachMap.put("username", parameters.get("username"));      // 用户名
            data.put("attach", JSON.toJSONString(attachMap));

            String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);// 将map转成xml并且签名

            // 通过HTTPClient发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(xmlParam);
            httpClient.setHttps(true);
            httpClient.post();

            // 获取响应的结果
            String strXML = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(strXML);
            map.put("out_trade_no", parameters.get("out_trade_no"));
            map.put("total_fee", parameters.get("total_fee"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @author 栗子
     * @Description 查询支付状态
     * @Date 10:50 2020/9/20
     * @param out_trade_no
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            // 调用查询订单接口地址
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";

            // 封装请求参数（map）
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);               // 公众账号
            data.put("mch_id", partner);            // 商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());//        随机字符串
            data.put("out_trade_no", out_trade_no); // 商户订单号
            String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);// 将map转成xml并且签名

            // 通过HTTPClient发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(xmlParam);
            httpClient.setHttps(true);
            httpClient.post();

            // 获取响应的结果
            String strXML = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(strXML);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
