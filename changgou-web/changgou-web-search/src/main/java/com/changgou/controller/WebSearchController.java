package com.changgou.controller;

import com.changgou.search.feign.SearchFeign;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName WebSearchController
 * @Description
 * @Author 传智播客
 * @Date 10:21 2020/9/12
 * @Version 2.1
 **/
@Controller
@RequestMapping("/search")
public class WebSearchController {

    @Autowired(required = false)
    private SearchFeign searchFeign;

    /**
     * @author 栗子
     * @Description 检索页面
     * @Date 10:23 2020/9/12
     * @param
     * @return java.lang.String
     **/
    @GetMapping("/list")
    public String list(@RequestParam(required = false) Map<String, String> searchMap, Model model){
        // 调用search服务
        Map<String, Object> resultMap = searchFeign.search(searchMap);
        model.addAttribute("resultMap", resultMap); // 回显结果数据
        model.addAttribute("searchMap", searchMap); // 回显检索条件

        // 拼接URL地址
        String url = getUrl(searchMap);
        model.addAttribute("url", url);

        // 构建分页对象
        long total = Long.parseLong(resultMap.get("totalElements").toString());         // 总条数
        int currentpage = Integer.parseInt(resultMap.get("currentpage").toString());    // 当前页码
        int pagesize = Integer.parseInt(resultMap.get("pagesize").toString());          // 每页显示的条数

        Page page = new Page(total, currentpage, pagesize);
        model.addAttribute("page", page);

        return "search";
    }

    private String getUrl(Map<String, String> searchMap) {
        // 基础的URL
        String url = "/search/list";
        url += "?";
        if (searchMap != null){
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                // url?keywords=黑马&category=老人机&brand=华为&pageNum=2
                if ("pageNum".equals(key)){
                    continue;   // 跳出本次循环
                }
                url += key + "=" + value + "&";
            }
            // 拼接完成后，多一个&
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
