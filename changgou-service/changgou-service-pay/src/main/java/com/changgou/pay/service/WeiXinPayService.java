package com.changgou.pay.service;

import java.util.Map;

/**
 * @ClassName WeiXinPayService
 * @Description
 * @Author 传智播客
 * @Date 10:03 2020/9/20
 * @Version 2.1
 **/
public interface WeiXinPayService {

    /**
     * @author 栗子
     * @Description 创建支付链接
     * @Date 10:06 2020/9/20
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    Map<String, String> createNative(Map<String, String> parameters);
//    Map<String, String> createNative(String out_trade_no, String total_fee);

    /**
     * @author 栗子
     * @Description 查询支付状态
     * @Date 10:50 2020/9/20
     * @param out_trade_no
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    Map<String,String> queryStatus(String out_trade_no);
}
