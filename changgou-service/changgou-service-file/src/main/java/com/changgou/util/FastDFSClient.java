package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @ClassName FastDFSClient
 * @Description 操作FastDFS的工具类
 * @Author 传智播客
 * @Date 10:48 2020/9/5
 * @Version 2.1
 **/
public class FastDFSClient {

    // 加载FastDFS的配置文件
    static {
        String conf_filename = "fdfs_client.conf";
        String  path = new ClassPathResource(conf_filename).getPath();
        // 加载FastDFS的配置文件
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author 栗子
     * @Description 文件上传
     * @Date 11:04 2020/9/5
     * @param fastDFSFile 文件内容
     * @return java.lang.String[]
     **/
    public static String[] uploadFile(FastDFSFile fastDFSFile){
        // 获取附件信息
        byte[] file_buff = fastDFSFile.getContent();    // 附件内容
        String file_ext_name = fastDFSFile.getExt();    // 附件扩展名称
        NameValuePair[] meta_list = new NameValuePair[1];   // 附件的备注
        meta_list[0] = new NameValuePair(fastDFSFile.getAuthor());
        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、通过跟踪服务器获取存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、完成附件上传
            // <img src="路径">
            // 数组：文件的路径  附件所在的组 + 附件所在的服务器地址
            String[] uploadResult = storageClient.upload_file(file_buff, file_ext_name, meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @author 栗子
     * @Description 获取跟踪服务器地址
     * @Date 11:20 2020/9/5
     * @param
     * @return java.lang.String
     **/
    public static String getTrackerServerUrl(){
        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();

            // 获取服务器地址
            String hostAddress = trackerServer.getInetSocketAddress().getAddress().getHostAddress();
            // 获取端口
            int port = ClientGlobal.getG_tracker_http_port();
            String url = "http://" + hostAddress + ":" + port;  // http://localhost:7001/
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author 栗子
     * @Description 文件下载
     * @Date 11:51 2020/9/5
     * @param group_name
     * @param remote_filename
     * @return byte[]
     **/
    public static byte[] downLoadFile(String group_name, String remote_filename){

        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、通过跟踪服务器获取存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、完成附件下载
            byte[] bytes = storageClient.download_file(group_name, remote_filename);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author 栗子
     * @Description 文件删除
     * @Date 11:57 2020/9/5
     * @param group_name       组名
     * @param remote_filename  附件所在的服务器地址
     * @return void
     **/
    public static void deleteFile(String group_name, String remote_filename){
        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、通过跟踪服务器获取存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、完成附件删除
            storageClient.delete_file(group_name, remote_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author 栗子
     * @Description 获取附件信息
     * @Date 12:04 2020/9/5
     * @param group_name
     * @param remote_filename
     * @return org.csource.fastdfs.FileInfo
     **/
    public static FileInfo getFileInfo(String group_name, String remote_filename){
        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、通过跟踪服务器获取存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、获取附件信息
            FileInfo fileInfo = storageClient.get_file_info(group_name, remote_filename);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author 栗子
     * @Description 获取存储服务器信息
     * @Date 12:10 2020/9/5
     * @param groupName
     * @return org.csource.fastdfs.StorageServer
     **/
    public static StorageServer getStorageServer(String groupName){
        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、获取存储服务器信息
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer, groupName);
            return storeStorage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @author 栗子
     * @Description 获取批量存储服务器信息
     * @Date 12:10 2020/9/5
     * @param groupName
     * @return org.csource.fastdfs.StorageServer
     **/
    public static StorageServer[] getStorageServers(String groupName){
        try {
            // 1、创建跟踪服务器的客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器的服务端
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、获取批量存储服务器信息
            StorageServer[] storeStorages = trackerClient.getStoreStorages(trackerServer, groupName);
            return storeStorages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
