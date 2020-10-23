package com.changgou.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.DeleteListenPoint;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;

import java.util.List;

/**
 * @ClassName CanalDataListener
 * @Description 数据库的监听器
 * @Author 传智播客
 * @Date 15:24 2020/9/8
 * @Version 2.1
 **/
@CanalEventListener
public class CanalDataListener {

    /**
     * @author 栗子
     * @Description 监控新增操作
     * @Date 15:26 2020/9/8
     * @param entryType  操作数据库的事件
     * @param rowData    行数据
     * @return void
     **/
    @InsertListenPoint
    public void eventDataByInsert(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){

        // 获取新增后的数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列明：" + columnName + "---列值：" + columnValue);
        }
    }


    /**
     * @author 栗子
     * @Description 监控更新操作
     * @Date 15:31 2020/9/8
     * @param entryType
     * @param rowData
     * @return void
     **/
    @UpdateListenPoint
    public void eventDataByUpdate(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列明：" + columnName + "---列值：" + columnValue);
        }
        System.out.println("--------------更新前后的数据----------");
        // 更新后的数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列明：" + columnName + "---列值：" + columnValue);
        }
    }

    /**
     * @author 栗子
     * @Description 监控删除操作
     * @Date 15:31 2020/9/8
     * @param entryType
     * @param rowData
     * @return void
     **/
    @DeleteListenPoint
    public void eventDataByDelete(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列明：" + columnName + "---列值：" + columnValue);
        }
    }

}
