package com.changgou;

import com.changgou.util.FastDFSClient;
import org.apache.commons.io.IOUtils;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageServer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @ClassName FastDFSTest
 * @Description 文件的其他操作
 * @Author 传智播客
 * @Date 11:51 2020/9/5
 * @Version 2.1
 **/
public class FastDFSTest {

    // 文件下载
    @Test
    public void testDownLoadFile() throws Exception {
        String group_name = "group1";
        String remote_filename = "M00/00/00/wKjThF9TBO6AG9CSAAL6RQQtvo0259.jpg";
        byte[] bytes = FastDFSClient.downLoadFile(group_name, remote_filename);

        // 将字节数组转成文件
        IOUtils.write(bytes, new FileOutputStream("D:\\97.jpg"));
    }

    // 文件删除
    @Test
    public void testDeleteFile() throws Exception {
        String group_name = "group1";
        String remote_filename = "M00/00/00/wKjThF9TBO6AG9CSAAL6RQQtvo0259.jpg";

        FastDFSClient.deleteFile(group_name, remote_filename);
    }

    // 获取附件信息
    @Test
    public void testGetFileInfo(){
        String group_name = "group1";
        String remote_filename = "M00/00/00/wKjThF9TDmqAEFEaAAL6RQQtvo0656.jpg";
        FileInfo fileInfo = FastDFSClient.getFileInfo(group_name, remote_filename);

        System.out.println("附件所在服务器地址：" + fileInfo.getSourceIpAddr());
        System.out.println("附件大小：" + fileInfo.getFileSize());
        System.out.println("附件上传时间：" + fileInfo.getCreateTimestamp());
    }


    // 获取存储服务器信息
    @Test
    public void testGetStorageServer(){
        String groupName = "group1";
        StorageServer storageServer = FastDFSClient.getStorageServer(groupName);

        System.out.println("存储服务器地址：" + storageServer.getInetSocketAddress().getAddress().getHostAddress());
        System.out.println("存储服务器端口：" + storageServer.getInetSocketAddress().getPort());
        System.out.println("存储服务器索引：" + storageServer.getStorePathIndex());
    }

    // 获取多个存储服务器信息
    @Test
    public void testGetStorageServers(){
        String groupName = "group1";
        StorageServer[] storageServers = FastDFSClient.getStorageServers(groupName);

        StorageServer storageServer = storageServers[0];
        System.out.println("存储服务器地址：" + storageServer.getInetSocketAddress().getAddress().getHostAddress());
        System.out.println("存储服务器端口：" + storageServer.getInetSocketAddress().getPort());
        System.out.println("存储服务器索引：" + storageServer.getStorePathIndex());
    }
}
