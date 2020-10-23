package com.changgou.search.service;

import java.util.Map; /**
 * @ClassName SearchService
 * @Description
 * @Author 传智播客
 * @Date 11:30 2020/9/9
 * @Version 2.1
 **/
public interface SearchService {

    /**
     * @author 栗子
     * @Description 商品检索
     * @Date 12:15 2020/9/9
     * @param searchMap
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    Map<String,Object> search(Map<String, String> searchMap);

    /**
     * @author 栗子
     * @Description 将数据库数据导入es中
     * @Date 11:30 2020/9/9
     * @param
     * @return void
     **/
    void importDataToEs();


}
