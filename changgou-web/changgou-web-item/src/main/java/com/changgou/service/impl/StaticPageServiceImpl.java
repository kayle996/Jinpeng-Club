package com.changgou.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.service.StaticPageService;
import com.changgou.util.SFTPUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName StaticPageServiceImpl
 * @Description
 * @Author 传智播客
 * @Date 15:47 2020/9/12
 * @Version 2.1
 **/
@Service
public class StaticPageServiceImpl implements StaticPageService{

    @Autowired
    private TemplateEngine templateEngine;  // 操作thymeleaf

    @Autowired(required = false)
    private SpuFeign spuFeign;

    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired(required = false)
    private CategoryFeign categoryFeign;

    @Value("${pagepath}")
    private String pagepath;    // 文件生成的位置

    /**
     * @author 栗子
     * @Description 生成静态页面
     * @Date 15:47 2020/9/12
     * @param id
     * @return void
     **/
    @Override
    public void index(Long id) {
        try {
            // 生成静态页面：数据 + 模板 = 输出
            Map<String, Object> dataMap = getDataMap(id);
            Context context = new Context();
            context.setVariables(dataMap);

            // 文件生成的位置
            File file = new File(pagepath);
            if (!file.exists()){
                file.mkdirs();
            }
            File desc = new File(file, id + ".html");
            PrintWriter writer = new PrintWriter(desc, "UTF-8");

            // arg0：指定模板名称   arg1：业务数据  arg2：输出流对象
            templateEngine.process("item", context, writer);

            // 获取文件的输出流
//            FileInputStream is = new FileInputStream(desc);
//
//            SFTPUtil sftpUtil = new SFTPUtil("root", "123456", "192.168.211.132", 22);
//            sftpUtil.upload("/usr/local", "/openresty/nginx/html", id + ".html", is);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取静态页面需要的数据
    private Map<String, Object> getDataMap(Long id) {
        Map<String, Object> map = new HashMap<>();
        // 获取商品信息（spu）
        Spu spu = spuFeign.findById(id).getData();
        map.put("spu", spu);
        // 获取商品库存（List<Sku>）
        List<Sku> skuList = skuFeign.findSkusbySpuId(id).getData();
        map.put("skuList", skuList);
        // 获取商品分类（category）
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        map.put("category1", category1);
        map.put("category2", category2);
        map.put("category3", category3);
        // 获取商品的小图
        String[] imageList = spu.getImages().split(",");
        map.put("imageList", imageList);
        // 获取商品的规格
        String json = spu.getSpecItems();
        Map<String, String> specificationList = JSON.parseObject(json, Map.class);
        map.put("specificationList", specificationList);

        return map;
    }
}
