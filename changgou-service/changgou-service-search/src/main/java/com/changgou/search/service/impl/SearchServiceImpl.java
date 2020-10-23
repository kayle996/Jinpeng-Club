package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuInfoDao;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @ClassName SearchServiceImpl
 * @Description
 * @Author 传智播客
 * @Date 11:31 2020/9/9
 * @Version 2.1
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;




    /**
     * @author 栗子
     * @Description 商品检索
     * @Date 12:15 2020/9/9
     * @param searchMap 检索条件
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        // 封装检索条件：单独抽取一个方法
        NativeSearchQueryBuilder nativeSearchQueryBuilder = builderBasicQuery(searchMap);
        // 查询商品列表数据
        Map<String, Object> resultMap = searchForPage(nativeSearchQueryBuilder);

        // 查询商品分类列表数据
//        List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);

//        Long totalElements = (Long) resultMap.get("totalElements");
//        int count = totalElements.intValue();
//        List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder, count);
//        resultMap.put("categoryList", categoryList);

        // 查询品牌列表数据
//        List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
//        resultMap.put("brandList", brandList);

        // 查询商品规格列表数据
//        Map<String, Set<String>> specList = searchSpecList(nativeSearchQueryBuilder);
//        resultMap.put("specList", specList);

        // 获取所有的分组结果
        Map<String, Object> groupMap = searchGroupList(nativeSearchQueryBuilder);
        resultMap.putAll(groupMap);
        return resultMap;
    }

    private Map<String, Object> searchGroupList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        // 添加多个分组条件
        nativeSearchQuery.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName").size(10000));
        nativeSearchQuery.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName").size(10000));
        nativeSearchQuery.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));

        // 根据条件进行分组查询
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);
        Aggregations aggregations = page.getAggregations();

        // 获取分组列表数据
        List<String> categoryList = getList("skuCategory", aggregations);
        List<String> brandList = getList("skuBrand", aggregations);
        List<String> specs = getList("skuSpec", aggregations);
        // 进一步处理规格
        Map<String, Set<String>> specList = pullMap(specs);

        // 数据封装
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", categoryList);
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }

    private List<String> getList(String groupName, Aggregations aggregations) {
        StringTerms stringTerms = aggregations.get(groupName);
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            list.add(brandName);
        }

        return list;
    }

    // 查询商品规格列表数据
    private Map<String,Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {

        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        // addAggregation：添加聚合查询的条件 terms：指定别名   field：根据哪个字段进行分组  size：指定查询的记录数（总条数）
        nativeSearchQuery.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);

        // 结果集处理
        Aggregations aggregations = page.getAggregations();
//        aggregations.
        StringTerms stringTerms = aggregations.get("skuSpec");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            list.add(brandName);
        }

        // 需要对列表数据进一步进行处理
        Map<String,Set<String>> map = pullMap(list);
        return map;

    }

    // 将规格列表数据封装到map中
    private Map<String, Set<String>> pullMap(List<String> list) {
        if (list != null && list.size() > 0){
            Map<String, Set<String>> map = new HashMap<>();
            for (String spec : list) {
                // 栗子：
                // {"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
                // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}
                // {"电视音响效果":"环绕","电视屏幕尺寸":"50英寸","尺码":"165"}
                // 将json转成map
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();        // 电视音响效果   电视音响效果
                    String value = entry.getValue();    // 小影院        立体声

                    // 封装到map中
                    Set<String> set = map.get(key);
                    if (set == null){
                        set = new HashSet<>();
                    }
                    set.add(value);
                    map.put(key, set);
                }
            }
            return map;
        }
        return null;
    }

    // 查询品牌列表数据
    private List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        // addAggregation：添加聚合查询的条件 terms：指定别名   field：根据哪个字段进行分组
        nativeSearchQuery.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName").size(10000));
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);

        // 结果集处理
        Aggregations aggregations = page.getAggregations();
//        aggregations.
        StringTerms stringTerms = aggregations.get("skuBrand");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            list.add(brandName);
        }

        return list;
    }

    // 查询商品分类列表数据
    private List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        // addAggregation：添加聚合查询的条件 terms：指定别名   field：根据哪个字段进行分组
        nativeSearchQuery.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName").size(10000));
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);

        // 结果集处理
        Aggregations aggregations = page.getAggregations();
//        aggregations.
        StringTerms stringTerms = aggregations.get("skuCategory");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            String categoryName = bucket.getKeyAsString();
            list.add(categoryName);
        }

        return list;
    }

    // 对检索的关键字进行高亮
    private Map<String, Object> searchForPage(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 对检索的关键字高亮显示：继续添加条件
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");  // 参数：指定哪个字段中包含的关键字进行高亮
        field.preTags("<font color='red'>");
        field.postTags("</font>");
        nativeSearchQueryBuilder.withHighlightFields(field);    // 添加高亮的条件

        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        // 分页对象
//        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);
        SearchResultMapper searchResultMapper = new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                List<T> content = new ArrayList<>();
                for (SearchHit hit : hits) {
                    String json = hit.getSourceAsString();  // 普通结果
                    SkuInfo skuInfo = JSON.parseObject(json, SkuInfo.class); // name：普通的结果
                    HighlightField highlightField = hit.getHighlightFields().get("name");
                    if (highlightField != null){
                        // 获取高亮的结果
                        Text[] texts = highlightField.getFragments();
                        String hightLightName = texts[0].toString();    // 高亮的名称
                        // 替换普通的名称
                        skuInfo.setName(hightLightName);

                        content.add((T)skuInfo);
                    }

                }
                return new AggregatedPageImpl<>(content, pageable, hits.getTotalHits());
            }
        };
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class, searchResultMapper);// searchResultMapper：包含了高亮结果
        // 结果集处理
        Map<String, Object> map = new HashMap<>();
        List<SkuInfo> rows = page.getContent();         // 结果集
        long totalElements = page.getTotalElements();   // 总条数
        int totalPages = page.getTotalPages();          // 总页数
        map.put("rows", rows);
        map.put("totalElements", totalElements);
        map.put("totalPages", totalPages);

        // 前端分页对象还需要当前页码、每页显示的条数
        int currentpage = page.getPageable().getPageNumber() + 1;
        int pagesize = page.getPageable().getPageSize();
        map.put("currentpage", currentpage);
        map.put("pagesize", pagesize);

        return map;
    }


    // 检索商品列表数据
//    private Map<String, Object> searchForPage(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
//        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
//        // 分页对象
//        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);
//        // 结果集处理
//        Map<String, Object> map = new HashMap<>();
//        List<SkuInfo> rows = page.getContent();         // 结果集
//        long totalElements = page.getTotalElements();   // 总条数
//        int totalPages = page.getTotalPages();          // 总页数
//        map.put("rows", rows);
//        map.put("totalElements", totalElements);
//        map.put("totalPages", totalPages);
//        return map;
//    }

    private NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder bool = new BoolQueryBuilder();
        if (searchMap != null){
            String keywords = searchMap.get("keywords");
            // 1、根据关键字检索
            if (!StringUtils.isEmpty(keywords)){
                queryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }

            // 2、根据商品分类进行过滤
            // http://localhost:18085/search?keywords=黑马&category=老人机
            String category = searchMap.get("category");
            if (!StringUtils.isEmpty(category)){
                bool.must(QueryBuilders.matchQuery("categoryName", category));
            }

            // 3、根据商品品牌进行过滤
            // http://localhost:18085/search?keywords=黑马&brand=华为
            String brand = searchMap.get("brand");
            if (!StringUtils.isEmpty(brand)){
                bool.must(QueryBuilders.matchQuery("brandName", brand));
            }

            // 4、根据商品规格进行过滤
            // http://localhost:18085/search?keywords=黑马&spec_内存=32G&spec_颜色=红色
            Set<String> keys = searchMap.keySet();
            for (String key : keys) {
                if (key.startsWith("spec_")){ // 只要以spec_开头的，都是规格条件
                    String option = searchMap.get(key); // 字段名称：specMap.内存.keyword
                    bool.must(QueryBuilders.matchQuery("specMap." + key.substring(5) + ".keyword", option));
                }
            }

            // 5、根据商品的价格区间段进行过滤
            String price = searchMap.get("price");  // 例如：0-500   500-100   2000以上
            if (!StringUtils.isEmpty(price)){
                String[] prices = price.split("-");
                bool.must(QueryBuilders.rangeQuery("price").gte(prices[0])); // >=
                if (prices.length == 2){
                    bool.must(QueryBuilders.rangeQuery("price").lte(prices[1]));    // <=
                }
            }

            // 6、根据条件进行排序
            // http://localhost:18085/search?keywords=黑马&sortField=price/updateTime&sortRule=ASC/DESC
            String sortField = searchMap.get("sortField");  // 排序的字段
            String sortRule = searchMap.get("sortRule");    // 排序规则
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
                queryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
//                if ("ASC".equals(sortRule)){
//                    queryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
//                }else {
//                    queryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
//                }
            }

        }

        // 添加过滤条件
        queryBuilder.withFilter(bool);

        // 对检索的结果进行分页
        String pageNum = searchMap.get("pageNum");
        if (StringUtils.isEmpty(pageNum)){
            // 默认第一页
            pageNum = "1";
        }
        Integer page = Integer.valueOf(pageNum) - 1;
        Integer size = 40;   // 每页显示的条数
        Pageable pageable = PageRequest.of(page, size); // of方法page参数：页码，默认从0页开始
        queryBuilder.withPageable(pageable);

        return queryBuilder;
    }

    /**
     * @author 栗子
     * @Description 将数据库数据导入es中
     * @Date 11:30 2020/9/9
     * @param
     * @return void
     **/
    @Override
    public void importDataToEs() {
        // 1、调用feign获取库存列表数据 List<Sku>
        List<Sku> skuList = skuFeign.findSkuListByStatus("1");
        if (skuList != null && skuList.size() > 0){
            // 2、将列表数据List<Sku>转成List<SkuInfo>
            String text = JSON.toJSONString(skuList);
            List<SkuInfo> skuInfos = JSON.parseArray(text, SkuInfo.class);
            for (SkuInfo skuInfo : skuInfos) {
                // {内存：32G,颜色：红色}
                String spec = skuInfo.getSpec();
                Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
                skuInfo.setSpecMap(specMap);
            }


            // 3、将库存列表数据保存到es中
            skuInfoDao.saveAll(skuInfos);
        }
    }
}
