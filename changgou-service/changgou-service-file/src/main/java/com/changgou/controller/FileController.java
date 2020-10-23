package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName FileController
 * @Description
 * @Author 传智播客
 * @Date 11:07 2020/9/5
 * @Version 2.1
 **/
@RestController
public class FileController {

    /**
     * @author 栗子
     * @Description 文件上传
     * @Date 11:08 2020/9/5
     * @param file 前端传递的附件信息
     * @return java.lang.String
     **/
    @RequestMapping("/upload")
    public String upload(MultipartFile file) throws Exception {
        String name = file.getOriginalFilename();       // 文件名称 xxx.jpg
        byte[] content = file.getBytes();               // 文件内容
        String ext = FilenameUtils.getExtension(name);  // 文件扩展名称
        // 构建附件对象
        FastDFSFile fastDFSFile = new FastDFSFile(name, content, ext, null, "tom");
        // 文件上传 组名称 + 所在服务的路径   /group1/M00/00/01/xxx.jpg
        String[] uploadResult = FastDFSClient.uploadFile(fastDFSFile);
        // 组装url  http://ip:port/group1/M00/00/01/xxx.jpg
        String uri = FastDFSClient.getTrackerServerUrl();
        // http://ip:port
        String url = uri + "/" + uploadResult[0] + "/" + uploadResult[1];
        return url;
    }
}
