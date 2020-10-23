package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName SkuInfoDao
 * @Description
 * @Author 传智播客
 * @Date 11:29 2020/9/9
 * @Version 2.1
 **/
public interface SkuInfoDao extends ElasticsearchRepository<SkuInfo, Long> {
    // TODO 后期扩展的方法
}
