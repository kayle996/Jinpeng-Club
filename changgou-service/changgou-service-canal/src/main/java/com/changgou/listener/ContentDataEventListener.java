package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @ClassName ContentDataEventListener
 * @Description 监控广告数据的变化
 * @Author 传智播客
 * @Date 16:16 2020/9/8
 * @Version 2.1
 **/
@CanalEventListener
public class ContentDataEventListener {

    @Autowired(required = false)
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @author 栗子
     * @Description 监控数据的变化
     * @Date 16:19 2020/9/8
     * @param entryType
     * @param rowData
     * @return void
     **/
    // destination：canal实例名称  schema：监控的数据库 table：监控的表  eventType：数据库的操作事件
    @ListenPoint(destination = "example", schema = {"changgou_content"},
            table = {"tb_content"}, eventType = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    public void onEventContentData(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){
        // 1、获取行数据中的category_id值
        String category_id = getCategoryId("category_id", rowData);

        // 2、通过ContentFeign调用，获取广告列表数据
        Result<List<Content>> result = contentFeign.findContentListByCategoryId(Long.parseLong(category_id));
        List<Content> list = result.getData();

        // 3、将最新的数据写入缓存
        stringRedisTemplate.boundValueOps("content_" + category_id).set(JSON.toJSONString(list));
    }

    private String getCategoryId(String columnName, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            String name = column.getName();
            if (name.equals(columnName)){
                String categoryId = column.getValue();
                return categoryId;
            }
        }
        return null;
    }
}
