package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/****
 * @Author:传智播客
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired(required = false)
    private SpuMapper spuMapper;

    @Autowired(required = false)
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired(required = false)
    private CategoryMapper categoryMapper;

    @Autowired(required = false)
    private BrandMapper brandMapper;


    /**
     * @author 栗子
     * @Description 保存商品
     * @Date 11:48 2020/9/6
     * @param goods
     * @return void
     **/
    @Override
    public void saveOrUpdate(Goods goods) {
        // 获取商品的基本信息
        Spu spu = goods.getSpu();
        if (!StringUtils.isEmpty(spu.getId())){
            // id存在：更新
            spu.setStatus("0"); // 更新后的商品需要重新审核
            spuMapper.updateByPrimaryKeySelective(spu);

            // 更新库存：先删除再插入
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }else{
            // id不存在：插入
            long id = idWorker.nextId();
            spu.setId(id);      // 设置主键
            spu.setIsMarketable("0");       // 未上架
            spu.setIsDelete("0");           // 未删除
            spu.setStatus("0");             // 未审核
            spuMapper.insertSelective(spu);
        }


        // 获取商品的库存信息
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null && skuList.size() > 0){
            for (Sku sku : skuList) {
                long skuId = idWorker.nextId();
                sku.setId(skuId);       // 主键
                // name = spu名称 + 副标题 + 规格
                String name = spu.getName() + " " + spu.getCaption();
                // eg：{"手机屏幕尺寸":"5寸","网络":"联通3G+移动4G","颜色":"红","机身内存":"16G","存储":"64G","像素":"800万像素"}
                String spec = sku.getSpec();    // 页面传递过来的规格数据
                // 将规格的json数据转成map
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                if (specMap != null){
                    Set<Map.Entry<String, String>> entries = specMap.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        String value = entry.getValue();
                        name = name + " " + value;
                    }
                }
                sku.setName(name);      // 商品库存的名称
                sku.setCreateTime(new Date());      // 商品录入时间
                sku.setUpdateTime(new Date());      // 商品更新时间
                sku.setSpuId(spu.getId());                   // spu的id
                sku.setCategoryId(spu.getCategory3Id());    // 商品分类id
                String categoryName = categoryMapper.selectByPrimaryKey(spu.getCategory3Id()).getName();
                sku.setCategoryName(categoryName);  // 商品分类名称
                sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName()); // 商品品牌名称
                sku.setStatus("1");
                skuMapper.insertSelective(sku);
            }
        }
    }

    /**
     * Spu条件+分页查询
     * @param spu 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu){
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     * @param spu
     * @return
     */
    public Example createExample(Spu spu){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu!=null){
            // 主键
            if(!StringUtils.isEmpty(spu.getId())){
                    criteria.andEqualTo("id",spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())){
                    criteria.andEqualTo("sn",spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())){
                    criteria.andLike("name","%"+spu.getName()+"%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())){
                    criteria.andEqualTo("caption",spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())){
                    criteria.andEqualTo("brandId",spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())){
                    criteria.andEqualTo("category1Id",spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())){
                    criteria.andEqualTo("category2Id",spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())){
                    criteria.andEqualTo("category3Id",spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())){
                    criteria.andEqualTo("templateId",spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())){
                    criteria.andEqualTo("freightId",spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())){
                    criteria.andEqualTo("image",spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())){
                    criteria.andEqualTo("images",spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())){
                    criteria.andEqualTo("saleService",spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())){
                    criteria.andEqualTo("introduction",spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())){
                    criteria.andEqualTo("specItems",spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())){
                    criteria.andEqualTo("paraItems",spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())){
                    criteria.andEqualTo("saleNum",spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())){
                    criteria.andEqualTo("commentNum",spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())){
                    criteria.andEqualTo("isMarketable",spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())){
                    criteria.andEqualTo("isEnableSpec",spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if(!StringUtils.isEmpty(spu.getIsDelete())){
                    criteria.andEqualTo("isDelete",spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if(!StringUtils.isEmpty(spu.getStatus())){
                    criteria.andEqualTo("status",spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * @author 栗子
     * @Description 回显商品信息
     * @Date 12:13 2020/9/6
     * @param id
     * @return com.changgou.goods.pojo.Goods
     **/
    @Override
    public Goods findGoodsById(Long id) {
        // 查询商品信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 查询库存信息
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);

        // 数据封装
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * @author 栗子
     * @Description 商品审核
     * @Date 14:46 2020/9/6
     * @param id
     * @param status
     * @return void
     **/
    @Override
        public void audit(Long id, String status) {

//        Spu spu = spuMapper.selectByPrimaryKey(id);
//        if (spu.getIsDelete().equals("1") || "2".equals(spu.getIsMarketable())){
//            throw new RuntimeException("已删除或已下架的商品不能进行审核");
//        }
//        spuMapper.updateByPrimaryKeySelective(spu);

            Spu spu = new Spu();
            spu.setId(id);
            spu.setStatus(status);
            spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * @author 栗子
     * @Description 商品的上架或者下架操作
     * @Date 15:04 2020/9/6
     * @param id
     * @param isMarketable 0:待上架  1：已上架  2：已下架
     * @return void
     **/
    @Override
    public void isShow(Long id, String isMarketable) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsMarketable(isMarketable);
        // 1、更新商品的上下架状态
        spuMapper.updateByPrimaryKeySelective(spu);

        if ("1".equals(isMarketable)){ // 上架操作
            // TODO 将商品信息保存到索引库
            // TODO 生成商品详情的静态页面
        }else{
            // TODO 将商品从索引库删除
            // TODO [可选]删除商品详情的静态页面
        }
    }

    @Override
    public void isDel(Long id, String isDelete) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsDelete(isDelete);
        // 1、更新商品的上下架状态
        spuMapper.updateByPrimaryKeySelective(spu);
    }
}
