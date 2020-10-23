package com.changgou.goods.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/spu")
@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     * @author 栗子
     * @Description 商品的删除与还原
     * @Date 15:13 2020/9/6
     * @param id
     * @param isDelete
     * @return entity.Result
     **/
    @GetMapping("/isDel/{id}/{isDelete}")
    public Result isDel(@PathVariable(value = "id") Long id, @PathVariable(value = "isDelete") String isDelete){
        spuService.isDel(id, isDelete);
        return new Result(true, StatusCode.OK, "商品的删除与还原操作");
    }


    /**
     * @author 栗子
     * @Description 商品的上架或者下架操作
     * @Date 15:03 2020/9/6
     * @param id
     * @param isMarketable
     * @return entity.Result
     **/
    @GetMapping("/isShow/{id}/{isMarketable}")
    public Result isShow(@PathVariable(value = "id") Long id, @PathVariable(value = "isMarketable") String isMarketable){
        spuService.isShow(id, isMarketable);
        return new Result(true, StatusCode.OK, "上下架操作成功");
    }

    /**
     * @author 栗子
     * @Description 商品审核
     * @Date 14:46 2020/9/6
     * @param id
     * @param status
     * @return entity.Result
     **/
    @GetMapping("/audit/{id}/{status}")
    public Result audit(@PathVariable(value = "id") Long id, @PathVariable(value = "status") String status){
        spuService.audit(id, status);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * @author 栗子
     * @Description 回显商品成功
     * @Date 12:11 2020/9/6
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Goods>
     **/
    @GetMapping("/findGoodsById/{id}")
    public Result<Goods> findGoodsById(@PathVariable(value = "id") Long id){
        Goods goods = spuService.findGoodsById(id);
        return new Result(true, StatusCode.OK, "回显商品成功", goods);
    }

    /**
     * @author 栗子
     * @Description 保存商品
     * @Date 11:47 2020/9/6
     * @param goods
     * @return entity.Result
     **/
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        spuService.saveOrUpdate(goods);
        return new Result(true, StatusCode.OK, "保存成功成功");
    }


    /***
     * 新增Spu数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Spu spu){
        //调用SpuService实现添加Spu
        spuService.add(spu);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * Spu分页条件搜索实现
     * @param spu
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  Spu spu, @PathVariable  int page, @PathVariable  int size){
        //调用SpuService实现分页条件查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(spu, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Spu分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用SpuService实现分页查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param spu
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Spu>> findList(@RequestBody(required = false)  Spu spu){
        //调用SpuService实现条件查询Spu
        List<Spu> list = spuService.findList(spu);
        return new Result<List<Spu>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用SpuService实现根据主键删除
        spuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Spu数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  Spu spu,@PathVariable Long id){
        //设置主键值
        spu.setId(id);
        //调用SpuService实现修改Spu
        spuService.update(spu);
        return new Result(true,StatusCode.OK,"修改成功");
    }



    /***
     * 根据ID查询Spu数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable(value = "id") Long id){
        //调用SpuService实现根据主键查询Spu
        Spu spu = spuService.findById(id);
        return new Result<Spu>(true,StatusCode.OK,"查询成功",spu);
    }

    /***
     * 查询Spu全部数据
     * @return
     */
    @GetMapping
    public Result<List<Spu>> findAll(){
        //调用SpuService实现查询所有Spu
        List<Spu> list = spuService.findAll();
        return new Result<List<Spu>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
