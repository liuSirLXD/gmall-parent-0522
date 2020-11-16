package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.TradeMarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:LiuSir
 * @Description:
 * @Date: Create in 20:33 2020-11-01
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class TrademarkApiController {
    @Autowired
    TradeMarkService tradeMarkService;
    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;
    //得到品牌列表
    @RequestMapping("baseTrademark/{pageNum}/{pageSize}")
    public Result baseTrademark(@PathVariable("pageNum")Long pageNum, @PathVariable("pageSize")Long pageSize){
        IPage<BaseTrademark> tradeMarkIPage = new Page<>();
        tradeMarkIPage.setSize(pageSize);
        tradeMarkIPage.setCurrent(pageNum);
        IPage<BaseTrademark> trademarks =  tradeMarkService.baseTrademark(tradeMarkIPage);
        return Result.ok(trademarks);
    }

    //添加品牌
    @RequestMapping("baseTrademark/save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkMapper.insert(baseTrademark);
        return Result.ok();
    }

    //修改品牌列表,得到修改品牌信息,用于回显的作用
    @RequestMapping("baseTrademark/get/{id}")
    public Result get(@PathVariable("id")Long id){
        QueryWrapper<BaseTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        BaseTrademark baseTrademark = baseTrademarkMapper.selectOne(wrapper);
        return Result.ok(baseTrademark);
    }

    //修改方法
    @PutMapping("baseTrademark/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkMapper.updateById(baseTrademark);
        return Result.ok();
    }

    //删除品牌列表
    @DeleteMapping("baseTrademark/remove/{id}")
    public Result remove(@PathVariable("id")Long id){
        QueryWrapper<BaseTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        baseTrademarkMapper.delete(wrapper);
        return Result.ok();
    }


    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> trademarks = tradeMarkService.getTrademarkList();
        return Result.ok(trademarks);
    }
}
